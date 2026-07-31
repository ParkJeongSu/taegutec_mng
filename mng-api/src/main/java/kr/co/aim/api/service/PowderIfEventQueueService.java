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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"pex","tex","scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderIfEventQueueService implements FactoryIfEventQueueStrategy {

    private final ObjectMapper objectMapper;
    private final IfEventQueueService ifEventQueueService;

    @Override
    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void enqueueIfEventQueue(Object vo) {
        // Java 17의 Pattern Matching 사용
        if (vo instanceof PowderEventQueueReportVo reportVo) {
            // save EventLog로 변경
            List<IfEventQueueDto> ifEventQueueDtoList = createEventQueueDto(reportVo);
            if(CollectionUtils.isNotEmpty(ifEventQueueDtoList)){
                for(IfEventQueueDto dto : ifEventQueueDtoList){
                    TransactionInfo tx = TransactionInfo.now(EventName.SAVE_INTERFACE_EVENT_LOG.getValue(),SystemName.MNG.getValue(), "");
                    // DTO 객체를 JSON 문자열로 직접 변환합니다.
                    String jsonPayload = "";
                    try {
                        //jsonPayload = objectMapper.writeValueAsString(dto);
                        jsonPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
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
                }
            }
        }else {
            log.error("잘못된 객체 타입이 전달되었습니다: {}", vo != null ? vo.getClass().getName() : "null");
        }

    }

    private List<IfEventQueueDto> createEventQueueDto(PowderEventQueueReportVo vo) {
        List<IfEventQueueDto> ifEventQueueDtoList = new ArrayList<>();
        String messageName = vo.getMessageName();
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            List<IfEventQueueDto> result = handleLoadCompleted(vo);
            if(ObjectUtils.isNotEmpty(result)){
                ifEventQueueDtoList.addAll(result);
            }
        }
        return ifEventQueueDtoList;
    }

    private List<IfEventQueueDto> handleLoadCompleted(PowderEventQueueReportVo vo){
        List<IfEventQueueDto>  ifEventQueueDtoList = new ArrayList<>();
        // TODO: 비지니스 로직 추가
        return  ifEventQueueDtoList;
    }
}
