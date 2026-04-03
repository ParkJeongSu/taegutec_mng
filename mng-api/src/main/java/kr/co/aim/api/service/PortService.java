package kr.co.aim.api.service;

import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.PortDefHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class PortService {

    private final HistoryService historyService;

    private final PortDefRepository portDefRepository;
    private final PortDefMapper portDefMapper;

    private final PortRepository portRepository;
    private final PortMapper portMapper;

    /**
     * port 의 사용 타입 변경시 보고
     * @param vo 받은 메시지
     */
    @Transactional
    public void transportStateChanged(TransportStateChangedVo vo) {
        PortTransportStateChangedCommand portCommand =
                PortTransportStateChangedCommand
                        .builder()
                        .transactionInfo(vo.getTx())
                        .portTransportStateName(PortTransportState.RESERVED_TO_LOAD.getValue())
                        .build();
        Port port = vo.getPort();
        port.transportStateChanged(portCommand);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);
    }


    @Transactional
    public List<Port> findByTransportState(String transportState) {
        return portRepository.findByTransportState(transportState);
    }

    @Transactional
    public Optional<Port> findPortByEquipmentNameAndPortName(String equipmentName,String portName) {
        return portRepository.findByEquipmentNameAndPortName(equipmentName,portName);
    }

    @Transactional
    public Optional<PortDef> findPortDefByEquipmentNameAndPortName(String equipmentName,String portName) {
        return portDefRepository.findByEquipmentNameAndPortName(equipmentName,portName);
    }

    @Transactional
    public Port save(Port port) {
        return portRepository.save(port);
    }

    @Transactional
    public PortDef save(PortDef portDef) {
        return portDefRepository.save(portDef);
    }

}