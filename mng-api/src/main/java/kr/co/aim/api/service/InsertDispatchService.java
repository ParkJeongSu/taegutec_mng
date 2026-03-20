package kr.co.aim.api.service;

import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.DispatchStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.UnLoadRequestCommand;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertDispatchService implements DispatchStrategy {

    private final HistoryService historyService;
    private final PortDefRepository portDefRepository;

    private final PortRepository portRepository;
    private final PortMapper portMapper;

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    public BaseMessage<TransportJobRequestBody> requestDispatch(BaseMessage<CarrierDispatchRequestBody> message) {
        return null;
    }

    /**
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        return null;
    }
}
