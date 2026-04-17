package kr.co.aim.api.service;

import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.TransportJobState;
import kr.co.aim.common.format.*;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.repository.TransportJobRepository;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class TransportJobService {

    // 구현체(Infra)가 아닌 인터페이스(Domain)에 의존
    private final TransportJobRepository transportJobRepository;
    private final HistoryService historyService;
    private final TransportJobMapper transportJobMapper;

    @Transactional(readOnly = true)
    public List<TransportJob> findActiveTransportJobs(String equipmentName,String portName) {
        List<String> transportJobStateList = new ArrayList<>();
        transportJobStateList.add(TransportJobState.REQUESTED.getValue());
        transportJobStateList.add(TransportJobState.ACCEPTED.getValue());
        transportJobStateList.add(TransportJobState.STARTED.getValue());
        // Validation TransportJob exists and transportJob State
        List<TransportJob> transportJobList = transportJobRepository.findByDestinationEquipmentNameAndDestinationPortNameAndTransportJobStateIn(
                equipmentName,
                portName,
                transportJobStateList
        );
        return transportJobList;
    }

    /**
     * 요청한 반송잡이 첫시작되는 시점 보고
     */
    @Transactional
    public List<TransportJob> createTransportJob(CreateTransportJobVo createTransportJobVo) {
        List<TransportJob> transportJobList = new ArrayList<>();
        for(TransportJobCreateCommand command : createTransportJobVo.getTransportJobCreateCommandList()){
            TransportJob transportJob = TransportJob.create(command);
            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
            transportJobList.add(transportJob);
        }

        return  transportJobList;
    }

    public TransportJobRequestListBody createTransportJobMessage(List<TransportJob> transportJobList) {

        List<TransportJobRequestBody> transportJobRequestBodies = new ArrayList<>();
        for(TransportJob transportJob : transportJobList){
            TransportJobRequestBody body = TransportJobRequestBody.builder()
                    .transportJobName(transportJob.getTransportJobName())
                    .transportType(transportJob.getTransportType())
                    .carrierName(transportJob.getCarrierName())
                    .carrierType(transportJob.getCarrierType())
                    .sourceEquipmentName(transportJob.getSourceEquipmentName())
                    .sourceZoneName(transportJob.getSourceZoneName())
                    .sourcePositionType(transportJob.getSourcePositionTypeName())
                    .sourcePositionName(transportJob.getSourcePositionName())
                    .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                    .destinationZoneName(transportJob.getDestinationZoneName())
                    .destinationPositionType(transportJob.getDestinationPositionTypeName())
                    .destinationPositionName(transportJob.getDestinationPositionName())
                    .priority(transportJob.getPriority() == null ? "" : transportJob.getPriority().toString())
                    .orderId(transportJob.getOrderId())
                    .requestSource(transportJob.getRequestSource())
                    .travelProfile(transportJob.getTravelProfile())
                    .build();
            transportJobRequestBodies.add(body);
        }

        return TransportJobRequestListBody.builder()
                .transportJobList(transportJobRequestBodies)
                .build();
    }

    public TransportJobRequestBody createTransportJobMessage(TransportJob transportJob) {

        return TransportJobRequestBody.builder()
                .transportJobName(transportJob.getTransportJobName())
                .transportType(transportJob.getTransportType())
                .carrierName(transportJob.getCarrierName())
                .carrierType(transportJob.getCarrierType())
                .sourceEquipmentName(transportJob.getSourceEquipmentName())
                .sourceZoneName(transportJob.getSourceZoneName())
                .sourcePositionType(transportJob.getSourcePositionTypeName())
                .sourcePositionName(transportJob.getSourcePositionName())
                .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                .destinationZoneName(transportJob.getDestinationZoneName())
                .destinationPositionType(transportJob.getDestinationPositionTypeName())
                .destinationPositionName(transportJob.getDestinationPositionName())
                .priority(transportJob.getPriority() == null ? "" : transportJob.getPriority().toString())
                .orderId(transportJob.getOrderId())
                .requestSource(transportJob.getRequestSource())
                .travelProfile(transportJob.getTravelProfile())
                .build();
    }

    @Transactional
    public Optional<TransportJob> findByTransportJobName(String transportJobName) {
        return transportJobRepository.findByTransportJobName(transportJobName);
    }

    @Transactional
    public List<TransportJob> findByCarrierNameAndTransportJobStateIn(String carrierName, List<String> transportJobStates) {
        return transportJobRepository.findByCarrierNameAndTransportJobStateIn(carrierName, transportJobStates);
    }

    @Transactional
    public TransportJob save(TransportJob transportJob) {
        return transportJobRepository.save(transportJob);
    }

    @Transactional
    Optional<TransportJob> findWithLockByTransportJobName(String transportJobName){
        return transportJobRepository.findWithLockByTransportJobName(transportJobName);
    }

}