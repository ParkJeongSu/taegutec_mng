package kr.co.aim.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.powder.IfEventQueueDto;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.IfEventQueueCreateCommand;
import kr.co.aim.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"pex","tex","scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderIfEventQueueService implements FactoryIfEventQueueStrategy {

    private final ObjectMapper objectMapper;
    private final IfEventQueueService ifEventQueueService;

    /**
     * 1. 큐에 처음 넣을 때 (신규 생성)
     * try{
     * InterfaceEventLogService.enqueue(vo);
     * }
     * catch(Exception e){
     * log.error("로그 저장 실패");
     * }
     * 위 방식으로 호출 해야함
     */
    @Override
    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void enqueueIfEventQueue(Object vo) {
        // Java 17의 Pattern Matching 사용
        if (vo instanceof PowderEventQueueReportVo reportVo) {
            // save EventLog로 변경
            Optional<IfEventQueueDto> optionalIfEventQueueDto = createEventQueueDto(reportVo);
            if(optionalIfEventQueueDto.isEmpty()){
                return;
            }
            IfEventQueueDto dto = optionalIfEventQueueDto.get();
            TransactionInfo tx = TransactionInfo.now(EventName.SAVE_INTERFACE_EVENT_LOG.getValue(), SystemName.MNG.getValue(), "",reportVo.getTx().eventTime());

            // DTO 객체를 JSON 문자열로 직접 변환합니다.
            String jsonPayload = "";
            try {
                jsonPayload = objectMapper.writeValueAsString(dto);
            } catch (JsonProcessingException e) {
                log.error("dto -> String error");
                // 로깅 및 예외 처리
                throw new RuntimeException("InterfaceEventLogDto를 JSON으로 변환하는 중 오류가 발생했습니다.", e);
            }
            log.info("Sending JSON Payload: {}", jsonPayload);
            IfEventQueueCreateCommand command =
                    IfEventQueueCreateCommand
                            .builder()
                            .transactionInfo(tx)
                            .eventType(dto.getEventType())
                            .payload(jsonPayload)
                            .ifStatus(IfEventQueueState.READY.getValue())
                            .carrierName(dto.getCarrierName())
                            .idocId(dto.getIdocId())
                            .orderId(dto.getOrderId())
                            .orderLineNumber(dto.getOrderLineNumber())
                            .retryCNT(0)
                            .errMSG("")
                            .createTime(tx.eventTime())
                            .build();
            IfEventQueue interfaceEventLog = IfEventQueue.create(command);
            ifEventQueueService.save(interfaceEventLog);
        }else {
            log.error("잘못된 객체 타입이 전달되었습니다: {}", vo != null ? vo.getClass().getName() : "null");
        }

    }

    private Optional<IfEventQueueDto> createEventQueueDto(PowderEventQueueReportVo vo) {
        String messageName = vo.getMessageName();
        Optional<PortDef> optionalPortDef = vo.getOptionalPortDef();
        Optional<Port> optionalPort = vo.getOptionalPort();
        String eventType = "";
        String transactionCode ="";
        String carrierName = vo.getCarrierName(); // 어떠한 경우에도 공백이 없음
        String idocId = "";
        String orderId = "";
        String orderLineNumber = "";
        String orderType = "";
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            // TODO: Message 에 따라서 EventQueueDto 생성
        }
        return Optional.empty();
    }
}
