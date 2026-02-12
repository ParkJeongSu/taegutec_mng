package kr.co.aim.api.service;

import kr.co.aim.api.dto.*;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.enums.TransportJobState;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.model.TransportJobDetail;
import kr.co.aim.domain.repository.TransportJobDetailRepository;
import kr.co.aim.domain.repository.TransportJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class TransportJobService {

    // 구현체(Infra)가 아닌 인터페이스(Domain)에 의존
    private final TransportJobRepository transportJobRepository;
    private final TransportJobDetailRepository transportJobDetailRepository;

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

        Optional<TransportJobDetail> optionalTransportJobDetail = transportJobDetailRepository.findByTransportJobDetailName(transportJobDetailName);

        if(optionalTransportJobDetail.isEmpty()){
            return;
        }
        TransportJobDetail transportJobDetail = optionalTransportJobDetail.get();


        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        DestinationChangedCommand command = DestinationChangedCommand.builder()
                .transactionInfo(tx)
                .newDestinationEquipmentName(newDestinationEquipmentName)
                .newDestinationPositionType(newDestinationPositionType)
                .newDestinationPositionName(newDestinationPositionName)
                .newDestinationZoneName(newDestinationZoneName)
                .build();

        transportJobDetail.destinationChanged(command);
        transportJobDetailRepository.save(transportJobDetail);
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

        String transportJobDetailName = message.getBody().getTransportJobName(); // DetailName
        String carrierName = message.getBody().getCarrierName();

        try {
            Optional<TransportJobDetail> optionalTransportJobDetail = transportJobDetailRepository.findByTransportJobDetailName(transportJobDetailName);

            if(optionalTransportJobDetail.isEmpty()){
                throw new EntityNotFoundException(TransportJobDetail.class,transportJobDetailName);
            }

            TransportJobDetail transportJobDetail = optionalTransportJobDetail.get();
            Optional<TransportJob> optionalTransportJob = transportJobRepository.findById(transportJobDetail.getTransportJobId());

            if(optionalTransportJob.isEmpty()){
                throw new EntityNotFoundException(TransportJob.class,transportJobDetailName);
            }

            TransportJob transportJob = optionalTransportJob.get();

            // TODO: reply message 생성후 반환
            String destinationEquipmentName = transportJob.getDestinationEquipmentName();

            return null;
        } catch (Exception e) {
            // TODO: reply message 생성 후 반환
            return null;
        }
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

        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String destinationEquipmentName = message.getBody().getDestinationEquipmentName();
        String carrierName = message.getBody().getCarrierName();

        // TODO: TransportJobRequestBody 에 transportJobName 추가 후 TransportJob 데이터 변경
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName("");

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand.builder()
                            .transportJobState(TransportJobState.COMPLETED.getValue())
                            .arrivedTime(LocalDateTime.now())
                            .build();
            transportJob.changeTransportJob(command);

            transportJobRepository.save(transportJob);
            // TODO: TransportJob History add
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
    public void transportJobReply(BaseMessage<TransportJobRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String destinationEquipmentName = message.getBody().getDestinationEquipmentName();
        String carrierName = message.getBody().getCarrierName();
        
        // TODO: TransportJobRequestBody 에 transportJobName 추가 후 TransportJob 데이터 변경
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName("");

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand.builder()
                            .transportJobState(TransportJobState.ACCEPTED.getValue())
                            .build();
            transportJob.changeTransportJob(command);

            transportJobRepository.save(transportJob);
            // TODO: TransportJob History add
        }

    }

    /**
     * SCS 로 반송 요청
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobRequest() {

    }

    /**
     * 요청한 반송잡이 첫시작되는 시점 보고
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String destinationEquipmentName = message.getBody().getDestinationEquipmentName();
        String carrierName = message.getBody().getCarrierName();

        // TODO: TransportJobRequestBody 에 transportJobName 추가 후 TransportJob 데이터 변경
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName("");

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand.builder()
                            .transportJobState(TransportJobState.STARTED.getValue())
                            .departedTime(LocalDateTime.now())
                            .build();
            transportJob.changeTransportJob(command);

            transportJobRepository.save(transportJob);
            // TODO: TransportJob History add
        }
    }


    // ============== [TransportJob] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public TransportJob createTransportJob(TransportJobCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(requestDto.getTransportJobName());
        if(optionalTransportJob.isPresent()){
            throw new EntityExistException("이미 생성된 Job 이름입니다. ID: " + requestDto.getTransportJobName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        TransportJobCreateCommand command =
                TransportJobCreateCommand.builder()
                        .transportJobName(requestDto.getTransportJobName())
                        .transactionInfo(tx)
                        .build();

        TransportJob transportJob = TransportJob.create(command);
        transportJob = transportJobRepository.save(transportJob);
        // TODO : TransPortJobHistory 추가 로직 넣기
        return transportJob;
    }

    @Transactional(readOnly = true)
    public Page<TransportJobResponseDto> findTransportJob(TransportJobSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<TransportJobResponseDto> page = null;//transportJobRepository.findTransportJobWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public TransportJob changeTransportJob(Long id, TransportJobUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        TransportJob transportJob;
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findById(id);
        if(optionalTransportJob.isPresent()){
            transportJob = optionalTransportJob.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설정입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        TransportJobUpdateCommand command =
                TransportJobUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        transportJob.changeTransportJob(command);

        return transportJobRepository.save(transportJob);
    }


    @Transactional
    public void deleteAllTransportJobByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        transportJobRepository.deleteAllByIdInBatch(ids);
    }



    // ============== [TransportJob] ==============



    // ============== [TransportJobDetail] ==============


    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public TransportJobDetail createTransportJobDetail(TransportJobDetailCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<TransportJobDetail> optionalTransportJobDetail = transportJobDetailRepository.findByTransportJobDetailName(requestDto.getTransportJobDetailName());
        if(optionalTransportJobDetail.isPresent()){
            throw new EntityExistException("이미 생성된 Job 이름입니다. ID: " + requestDto.getTransportJobDetailName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        TransportJobDetailCreateCommand command =
                TransportJobDetailCreateCommand.builder()
                        .transportJobDetailName(requestDto.getTransportJobDetailName())
                        .transactionInfo(tx)
                        .build();

        TransportJobDetail transportJobDetail = TransportJobDetail.create(command);

        return transportJobDetailRepository.save(transportJobDetail);
    }

    @Transactional(readOnly = true)
    public Page<TransportJobDetailResponseDto> findTransportJobDetail(TransportJobDetailSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<TransportJobDetailResponseDto> page = null;//transportJobDetailRepository.findTransportJobDetailWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public TransportJobDetail changeTransportJobDetail(Long id, TransportJobDetailUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        TransportJobDetail transportJobDetail;
        Optional<TransportJobDetail> optionalTransportJobDetail = transportJobDetailRepository.findById(id);
        if(optionalTransportJobDetail.isPresent()){
            transportJobDetail = optionalTransportJobDetail.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 설정입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        TransportJobDetailUpdateCommand command =
                TransportJobDetailUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        transportJobDetail.changeTransportJobDetail(command);

        return transportJobDetailRepository.save(transportJobDetail);
    }


    @Transactional
    public void deleteAllTransportJobDetailByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        transportJobDetailRepository.deleteAllByIdInBatch(ids);
    }

    // ============== [TransportJobDetail] ==============







}