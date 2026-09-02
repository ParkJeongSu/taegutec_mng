package kr.co.aim.api.service;

import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class PortService {

    private final HistoryService historyService;
    private final PortRepository portRepository;
    private final PortMapper portMapper;

    /**
     * port 의 사용 타입 변경시 보고
     * @param vo 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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


    @Transactional(value = "mssqlTransactionManager")
    public List<Port> findByTransportState(String transportState) {
        return portRepository.findByTransportState(transportState);
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<Port> findEarliestPortPerWorkCenter(String transportState,List<String> detailPortType){
        return portRepository.findEarliestPortPerWorkCenter(transportState,detailPortType);
    }


    @Transactional(value = "mssqlTransactionManager")
    public Port save(Port port) {
        return portRepository.save(port);
    }

}