package kr.co.aim.api.service;

import kr.co.aim.api.dto.InterfaceEventLogDto;
import kr.co.aim.api.vo.insert.ops.InsertEventLogReportVo;
import kr.co.aim.common.enums.InterfaceEventLogState;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.InterfaceEventLogCreateCommand;
import kr.co.aim.domain.model.InterfaceEventLog;
import kr.co.aim.domain.repository.InterfaceEventLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
public class InterfaceEventLogService {
    private final InterfaceEventLogRepository interfaceEventLogRepository;
    private final FactoryProcessStrategy factoryProcessStrategy;

    // 조회하는거
    @Transactional(value = "db2TransactionManager")
    public List<InterfaceEventLog> findByIfStatusOrderByCreateTimeAsc(String ifStatus){
        return interfaceEventLogRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus);
    }

    // save 하는거 H2TransReportVo < 이걸로
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public InterfaceEventLogCreateCommand saveInterfaceEventLog(InsertEventLogReportVo vo) {

        // 내부에서 저장까지 하도록 할까..?
        factoryProcessStrategy.createEventLogCommand(vo);

        TransactionInfo tx = TransactionInfo.now("","","");
        InterfaceEventLogDto dto =
                InterfaceEventLogDto
                        .builder()
                        .messageName()
                        .transactionCode()
                        .carrierName()
                        .idocId()
                        .orderId()
                        .orderLineNumber()
                        .errorText()
                        .actualWeight()
                        .actualZoneName()
                        .actualRackLocationId()
                        .build();
        InterfaceEventLogCreateCommand command =
                InterfaceEventLogCreateCommand
                        .builder()
                        .transactionInfo(tx)
                        .eventType(dto.getMessageName())
                        .payload()
                        .ifStatus(InterfaceEventLogState.READY.getValue())
                        .carrierName(vo.getCarrierName())
                        .idocId(vo.getSourceIdoc().getLineId())
                        .orderId(vo.getOrderId())
                        .orderLineNumber()
                        .retryCNT(0)
                        .errMSG(0)
                        .createTime(tx.eventTime())
                        .build();
        InterfaceEventLog interfaceEventLog = InterfaceEventLog.create(command);
        interfaceEventLogRepository.save(interfaceEventLog);

    }
}