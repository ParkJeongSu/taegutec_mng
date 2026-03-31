package kr.co.aim.api.service;

import kr.co.aim.api.vo.insert.sim.H2TransReportVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.TransportJobState;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
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
    private final Optional<InsertExternalInterfaceService> insertExternalInterfaceService;

    /**
     * SCS 시스템이 켜질때 보고
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void activeTransportJobReport(BaseMessage<ActiveTransportJobReportBody> message) {
        log.info("activeTransportJobReport");
        // TODO: 이런 시나리오가 있는지 일단 확인
    }

    /**
     * SCS 의 목적지 변경 후 보고 메시지
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void destinationChanged(BaseMessage<DestinationChangedBody> message) {
        log.info("destinationChanged");

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String transportJobDetailName = message.getBody().getTransportJobName(); // DetailName
        String carrierName = message.getBody().getCarrierName();
        String oldDestinationEquipmentName = message.getBody().getOldDestinationEquipmentName();
        String oldDestinationPositionType = message.getBody().getOldDestinationPositionType();
        String oldDestinationPositionName = message.getBody().getOldDestinationPositionName();
        String oldDestinationZoneName = message.getBody().getOldDestinationZoneName();
        String newDestinationEquipmentName = message.getBody().getNewDestinationEquipmentName();
        String newDestinationPositionType = message.getBody().getNewDestinationPositionType();
        String newDestinationPositionName = message.getBody().getNewDestinationPositionName();
        String newDestinationZoneName = message.getBody().getNewDestinationZoneName();

    }

    /**
     * 스테이션 영역 도착 후 최종목적지 요청
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<DestinationReplyBody> destinationRequest(BaseMessage<DestinationRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String transportJobDetailName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        return null;
    }

    /**
     * SCS 에서 Warehouse 의 Zone 정보 변경시 보고
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void inventoryZoneDataReport(BaseMessage<InventoryZoneDataReport> message) {
        // TODO: 이걸 MNG 가 알아야할 필요가 있을까? 그냥 테이블 공유 하고 실시간으로 사용하면 되는걸까..
    }

    /**
     * 반송 취소처리 완료 보고
     *
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobCancelCompleted() {
        // TODO: 반송이 취소처리가 완료되는 시나리오가 있을까?
    }

    /**
     * 반송 취소처리 실패 보고 이 경우 그러면 최종적으로 어떻게 되는거지?
     *
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobCancelFailed() {
        // TODO: 반송이 취소처리가 완료되는 시나리오가 있을까?
    }

    /**
     * 반송 취소처리 시작 보고
     * 내가 걱정하는게 이 취소 시나리오가 Mixing 같이 여러개의 job이 만들어진 시나리오에서 어떻게 해야할지 고민..
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobCancelStarted() {
        // TODO: 반송이 취소처리가 완료되는 시나리오가 있을까?
    }

    /**
     * 반송 잡이 정상적으로 종료되었음을 보고
     * 1. transportDetailJob 조회 
     * 2. transportDetailJob 상태 completed 로 변경
     * 3. 모든 transportDetailJob 이 completed 라면, transportJob -> completed 변경
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.COMPLETED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
        }
    }

    /**
     * 반송 잡의 처리 여부를 반환
     * reply 가 정상처리라면,
     * 반송잡의 상태를 Accepted 로 변경
     * 
     * reply 가 실패처리라면
     * 반송잡의 상태를 rejected로 변경
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobReply(BaseMessage<TransportJobReplyListBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        List<TransportJobReplyBody> transportJobList = message.getBody().getTransportJobList();

        for(TransportJobReplyBody transportJobReplyBody : transportJobList){
            Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobReplyBody.getTransportJobName());

            if(optionalTransportJob.isPresent()){
                TransportJob transportJob = optionalTransportJob.get();
                TransportJobUpdateCommand command =
                        TransportJobUpdateCommand
                                .builder()
                                .transportJobState(TransportJobState.ACCEPTED.getValue())
                                .transactionInfo(tx)
                                .build();
                transportJob.changeTransportJob(command);

                transportJob = transportJobRepository.save(transportJob);
                TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
                historyService.saveHistory(transportJobHistoryEntity);

                if(insertExternalInterfaceService.isPresent()){
                    H2TransReportVo h2TransReportVo =
                            H2TransReportVo
                                    .builder()
                                    .transportJobName(transportJob.getTransportJobName())
                                    .messageName(message.getMessageName())
                                    .carrierName(transportJob.getCarrierName())
                                    .orderId(transportJob.getOrderId())
                                    .build();
                    InsertExternalInterfaceService insertService = insertExternalInterfaceService.get();
                    insertService.reportH2trans(h2TransReportVo);
                }
            }
        }
        


    }

    /**
     * 요청한 반송잡이 첫시작되는 시점 보고
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.STARTED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            if(insertExternalInterfaceService.isPresent()){
                H2TransReportVo h2TransReportVo =
                        H2TransReportVo
                                .builder()
                                .transportJobName(transportJobName)
                                .messageName(message.getMessageName())
                                .carrierName(transportJob.getCarrierName())
                                .orderId(transportJob.getOrderId())
                                .build();
                InsertExternalInterfaceService insertService = insertExternalInterfaceService.get();
                insertService.reportH2trans(h2TransReportVo);
            }
        }
    }

    @Transactional(readOnly = true) // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
                    .carrierName(transportJob.getCarrierName())
                    .transportType(transportJob.getTransportType())
                    .carrierType(transportJob.getCarrierType())
                    .drivingProfile(transportJob.getDrivingProfile())
                    .sourceEquipmentName(transportJob.getSourceEquipmentName())
                    .sourcePortName(transportJob.getSourcePortName())
                    .sourceZoneName(transportJob.getSourceZoneName())
                    .sourcePositionTypeName(transportJob.getSourcePositionTypeName())
                    .sourcePositionName(transportJob.getSourcePositionName())
                    .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                    .destinationPortName(transportJob.getDestinationPortName())
                    .destinationZoneName(transportJob.getDestinationZoneName())
                    .destinationPositionTypeName(transportJob.getDestinationPositionTypeName())
                    .destinationPositionName(transportJob.getDestinationPositionName())
                    .priority(transportJob.getPriority() == null ? "" : transportJob.getPriority().toString())
                    .orderId(transportJob.getOrderId())
                    .build();
            transportJobRequestBodies.add(body);
        }

        return TransportJobRequestListBody.builder()
                .transportJobList(transportJobRequestBodies)
                .build();
    }

    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public TransportJob findByTransportJobName(String transportJobName) {
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            return optionalTransportJob.get();
        }
        throw new RuntimeException("TransportJob을 찾을 수 없습니다. (요청 ID: " + transportJobName + ")");
    }
}