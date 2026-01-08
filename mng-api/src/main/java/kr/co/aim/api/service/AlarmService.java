package kr.co.aim.api.service;

import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.AlarmState;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.AlarmReportBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.NotificationHandler;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.AlarmHistoryEntity;
import kr.co.aim.infra.persistence.mapper.AlarmMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class AlarmService {

    private final AlarmDefRepository alarmDefRepository; // 구현체(Infra)가 아닌 인터페이스(Domain)에 의존
    private final AlarmRepository alarmRepository;
    private final AlarmActionUserGroupRepository alarmActionUserGroupRepository;
    private final AlarmActionUserGroupUsersRepository alarmActionUserGroupUsersRepository;
    private final AlarmActionRepository alarmActionRepository;
    private final AlarmMailActionDetailRepository alarmMailActionDetailRepository;
    private final AlarmMapper alarmMapper;
    private final HistoryService historyService;

    private final Map<String, NotificationHandler> notificationServices;


    // ============== [확인용 코드 추가] ==============
    //    @PostConstruct
    //    public void checkProxy() {
    //        log.info("### Injected AlarmRepository Class: {}", alarmRepository.getClass().getName());
    //    }
    // ===============================================

    /**
     * 알람을 기록합니다.
     * 1. 알람 정의 find by AlarmCode
     * 만일 알람 정의가 없다면 종료
     * <p>
     * 2. equipmentName 과 alarmCode 로 Alarm find
     * 만일 없다면, 생성
     * 있다면, 변경
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void alarmReport(BaseMessage<AlarmReportBody> message) {
        String alarmCode = message.getBody().getAlarmCode();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        
        String equipmentName = message.getBody().getEquipmentName();
        String alarmState = message.getBody().getAlarmState();

        // 1. alarmDefRepository 통해 AlarmDef Domain 객체를 가져온다.
        Optional<AlarmDef> optionalAlarmDef = alarmDefRepository.findByAlarmDefName(alarmCode);
        if(optionalAlarmDef.isEmpty()) {
            log.info("alarm Def not Exists");
            return;
        }

        AlarmDef alarmDef = optionalAlarmDef.get();
        Optional<AlarmState> optionalAlarmState = AlarmState.fromValue(alarmState);
        AlarmState state;
        if(optionalAlarmState.isPresent()){
            state = optionalAlarmState.get();
        }
        else{
            log.info("AlarmState not Exists");
            return;
        }


        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        // 2. AlarmDefId 와 설비Id를 통해  Optional<Alarm> alarm 을 찾는다.
        // 위 데이터는 없을수도 있고 있을수도 있음
        // 만일 없다면, 생성
        // 있다면 변경한다.
        // 도메인 객체를 찾거나, 없으면 새로 생성해서 로직을 위임
        // TODO: 현재는 equipmentId 에 1L 강제 하드코딩 equipments 테이블이 생성됨에 따라 해당 로직 수정
        Optional<Alarm> optionalAlarm = alarmRepository.findByAlarmDefIdAndEquipmentName(alarmDef.getId(),equipmentName);
        Alarm alarm;
        if(optionalAlarm.isPresent()){
            AlarmReportCommand command = AlarmReportCommand.builder().alarmState(state).transactionInfo(tx).build();
            alarm = optionalAlarm.get();
            alarm.alarmReport(command);
        }
        else{
            AlarmCreateCommand command = AlarmCreateCommand.builder()
                    .alarmDefId(alarmDef.getId())
                    .equipmentName(equipmentName)
                    .alarmState(state)
                    .transactionInfo(tx)
                    .build();
            alarm = Alarm.create(command);
        }

        // 3. Repository를 통해 변경된 Domain 객체를 저장한다.
        // @Transactional 어노테이션의 '변경 감지(Dirty Checking)' 기능 덕분에
        // 이 save 호출은 사실 생략 가능할 때도 있지만, 명시적으로 호출하는 것이 좋습니다.
        alarm = alarmRepository.save(alarm);
        AlarmHistoryEntity alarmHistoryEntity = alarmMapper.toHistoryEntity(alarm);
        historyService.saveHistory(alarmHistoryEntity);


        // TODO: Alarm Action 현재 Alarm Action이 users 테이블이 없어서 강제로 유저 하드코딩 중 추후 수정
//        List<AlarmAction> alarmActionList = alarmActionRepository.findByAlarmDefId(alarmDef.getId());
//        for(AlarmAction alarmAction : alarmActionList){
//            alarmAction.execute(notificationServices);
//        }

    }


    // ============== [AlarmDef] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmDef createAlarmDef(AlarmDefCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<AlarmDef> optionalAlarmDef = alarmDefRepository.findByAlarmDefName(requestDto.getAlarmDefName());
        if(optionalAlarmDef.isPresent()){
            throw new EntityExistException("이미 생성된 알람입니다. ID: " + requestDto.getAlarmDefName());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmDefCreateCommand command =
                AlarmDefCreateCommand.builder()
                        .alarmDefName(requestDto.getAlarmDefName())
                        .alarmType(requestDto.getAlarmType())
                        .alarmLevel(requestDto.getAlarmLevel())
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        AlarmDef alarmDef = AlarmDef.create(command);

        return alarmDefRepository.save(alarmDef);
    }

    @Transactional(readOnly = true)
    public Page<AlarmDefResponseDto> findAlarmDefs(AlarmDefSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmDefResponseDto> page = alarmDefRepository.findAlarmDefWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmDef changeAlarmDef(Long id, AlarmDefUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        AlarmDef alarmDef;
        Optional<AlarmDef> optionalAlarmDef = alarmDefRepository.findById(id);
        if(optionalAlarmDef.isPresent()){
            alarmDef = optionalAlarmDef.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 알람입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmDefUpdateCommand command =
                AlarmDefUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        alarmDef.changeAlarmDef(command);

        return alarmDefRepository.save(alarmDef);
    }


    @Transactional
    public void deleteAllAlarmDefByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmDefRepository.deleteAllByIdInBatch(ids);
    }


    // ============== [AlarmDef] ==============


    // ============== [Alarm] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Alarm createAlarm(AlarmCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<Alarm> optionalAlarm = alarmRepository.findByAlarmDefIdAndEquipmentName(requestDto.getAlarmDefId(),requestDto.getEquipmentName());
        if(optionalAlarm.isPresent()){
            throw new EntityExistException("이미 생성된 알람입니다. ID: " + requestDto.getId());
        }

        String eventName = EventName.CREATED.getValue();

        Optional<AlarmState> optionalAlarmState = AlarmState.fromValue(requestDto.getAlarmState());
        AlarmState state;
        if(optionalAlarmState.isPresent()){
            state = optionalAlarmState.get();
        }
        else{
            log.info("AlarmState not Exists");
            throw new EntityNotFoundException("AlarmState 가 존재하지 않습니다.");
        }

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmCreateCommand command =
                AlarmCreateCommand.builder()
                        .alarmDefId(requestDto.getAlarmDefId())
                        .equipmentName(requestDto.getEquipmentName())
                        .alarmState(state)
                        .transactionInfo(tx)
                        .build();

        Alarm alarm = Alarm.create(command);

        return alarmRepository.save(alarm);
    }

    @Transactional(readOnly = true)
    public Page<AlarmResponseDto> findAlarms(AlarmSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmResponseDto> page = alarmRepository.findAlarmWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Alarm changeAlarm(Long id, AlarmUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Alarm alarm;
        Optional<Alarm> optionalAlarm = alarmRepository.findById(id);
        if(optionalAlarm.isPresent()){
            alarm = optionalAlarm.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 알람입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmUpdateCommand command =
                AlarmUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        alarm.changeAlarm(command);

        return alarmRepository.save(alarm);
    }


    @Transactional
    public void deleteAllAlarmByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmRepository.deleteAllByIdInBatch(ids);
    }

    // ============== [Alarm] ==============


    // ============== [AlarmActionUserGroup] ==============


    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmActionUserGroup createAlarmActionUserGroup(AlarmActionUserGroupCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<AlarmActionUserGroup> optionalAlarmActionUserGroup = alarmActionUserGroupRepository.findByUserGroupName(requestDto.getUserGroupName());
        if(optionalAlarmActionUserGroup.isPresent()){
            throw new EntityExistException("이미 생성된 유저 그룹입니다. ID: " + requestDto.getId());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmActionUserGroupCreateCommand command =
                AlarmActionUserGroupCreateCommand.builder()
                        .userGroupName(requestDto.getUserGroupName())
                        .transactionInfo(tx)
                        .build();

        AlarmActionUserGroup alarmActionUserGroup = AlarmActionUserGroup.create(command);

        return alarmActionUserGroupRepository.save(alarmActionUserGroup);
    }

    @Transactional(readOnly = true)
    public Page<AlarmActionUserGroupResponseDto> findAlarmActionUserGroups(AlarmActionUserGroupSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmActionUserGroupResponseDto> page = alarmActionUserGroupRepository.findAlarmUserGroupWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmActionUserGroup changeAlarmActionUserGroup(Long id, AlarmActionUserGroupUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        AlarmActionUserGroup alarmActionUserGroup;
        Optional<AlarmActionUserGroup> optionalAlarmActionUserGroup = alarmActionUserGroupRepository.findById(id);
        if(optionalAlarmActionUserGroup.isPresent()){
            alarmActionUserGroup = optionalAlarmActionUserGroup.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 알람입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmActionUserGroupUpdateCommand command =
                AlarmActionUserGroupUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        alarmActionUserGroup.changeAlarmActionUserGoup(command);

        return alarmActionUserGroupRepository.save(alarmActionUserGroup);
    }


    @Transactional
    public void deleteAllAlarmActionUserGroupByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmActionUserGroupRepository.deleteAllByIdInBatch(ids);
    }



    // ============== [AlarmActionUserGroup] ==============


    // ============== [AlarmActionUserGroupUsers] ==============


    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmActionUserGroupUsers createAlarmActionUserGroupUsers(AlarmActionUserGroupUsersCreateRequestDto requestDto) {

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmActionUserGroupUsersCreateCommand command =
                AlarmActionUserGroupUsersCreateCommand.builder()
                        .alarmActionUserGroupId(requestDto.getAlarmActionUserGroupId())
                        .userId(requestDto.getUserId())
                        .transactionInfo(tx)
                        .build();

        AlarmActionUserGroupUsers alarmActionUserGroupUsers = AlarmActionUserGroupUsers.create(command);

        return alarmActionUserGroupUsersRepository.save(alarmActionUserGroupUsers);
    }

    @Transactional(readOnly = true)
    public Page<AlarmActionUserGroupUsersResponseDto> findAlarmActionUserGroupsUsers(AlarmActionUserGroupUsersSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmActionUserGroupUsersResponseDto> page = alarmActionUserGroupUsersRepository.findAlarmActionUserGroupUsersWithConditions(condition,pageable);

        return page;
    }


    @Transactional
    public void deleteAllAlarmActionUserGroupUsersByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmActionUserGroupRepository.deleteAllByIdInBatch(ids);
    }


    // ============== [AlarmActionUserGroupUsers] ==============




    // ============== [AlarmAction] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmAction createAlarmAction(AlarmActionCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<AlarmAction> optionalAlarmAction = alarmActionRepository.findByAlarmActionName(requestDto.getAlarmActionName());
        if(optionalAlarmAction.isPresent()){
            throw new EntityExistException("이미 생성된 알람액션입니다. ID: " + requestDto.getId());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmActionCreateCommand command =
                AlarmActionCreateCommand.builder()
                        .alarmActionName(requestDto.getAlarmActionName())
                        .actionType(requestDto.getActionType())
                        .alarmDefId(requestDto.getAlarmDefId())
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        AlarmAction alarmAction = AlarmAction.create(command);

        return alarmActionRepository.save(alarmAction);
    }

    @Transactional(readOnly = true)
    public Page<AlarmActionResponseDto> findAlarmActions(AlarmActionSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmActionResponseDto> page = alarmActionRepository.findAlarmActionWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmAction changeAlarmActio(Long id, AlarmActionUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        AlarmAction alarmAction;
        Optional<AlarmAction> optionalAlarmAction = alarmActionRepository.findById(id);
        if(optionalAlarmAction.isPresent()){
            alarmAction = optionalAlarmAction.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 알람입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmActionUpdateCommand command =
                AlarmActionUpdateCommand.builder()
                        .transactionInfo(tx)
                        .build();

        alarmAction.changeAlarmAction(command);

        return alarmActionRepository.save(alarmAction);
    }


    @Transactional
    public void deleteAllAlarmActioByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmActionRepository.deleteAllByIdInBatch(ids);
    }

    // ============== [AlarmAction] ==============



    // ============== [AlarmMailActionDetail] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmMailActionDetail createAlarmMailActionDetail(AlarmActionDetailCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<AlarmMailActionDetail> optionalAlarmMailActionDetail =
                alarmMailActionDetailRepository.findByAlarmActionIdAndAlarmActionUserGroupId(requestDto.getAlarmActionId(),requestDto.getAlarmActionUserGroupId());
        if(optionalAlarmMailActionDetail.isPresent()){
            throw new EntityExistException("이미 생성된 알람메일디테일입니다. ID: " + requestDto.getId());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmMailActionDetailCreateCommand command =
                AlarmMailActionDetailCreateCommand.builder()
                        .alarmActionId(requestDto.getAlarmActionId())
                        .alarmActionUserGroupId(requestDto.getAlarmActionUserGroupId())
                        .subject(requestDto.getSubject())
                        .contents(requestDto.getContents())
                        .transactionInfo(tx)
                        .build();

        AlarmMailActionDetail alarmMailActionDetail = AlarmMailActionDetail.create(command);

        return alarmMailActionDetailRepository.save(alarmMailActionDetail);
    }

    @Transactional(readOnly = true)
    public Page<AlarmActionDetailResponseDto> findAlarmMailActionDetails(AlarmActionDetailSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.

        Page<AlarmActionDetailResponseDto> page = alarmMailActionDetailRepository.findAlarmMailActionDetailWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public AlarmMailActionDetail changeAlarmMailActionDetail(Long id, AlarmActionDetailUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        AlarmMailActionDetail alarmMailActionDetail;
        Optional<AlarmMailActionDetail> optionalAlarmMailActionDetail = alarmMailActionDetailRepository.findById(id);
        if(optionalAlarmMailActionDetail.isPresent()){
            alarmMailActionDetail = optionalAlarmMailActionDetail.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 알람메일디테일입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        AlarmMailActionDetailUpdateCommand command =
                AlarmMailActionDetailUpdateCommand.builder()
                        .subject(requestDto.getSubject())
                        .contents(requestDto.getContents())
                        .transactionInfo(tx)
                        .build();

        alarmMailActionDetail.changeAlarmMailActionDetail(command);

        return alarmMailActionDetailRepository.save(alarmMailActionDetail);
    }


    @Transactional
    public void deleteAllAlarmMailActionDetailByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        alarmMailActionDetailRepository.deleteAllByIdInBatch(ids);
    }

    // ============== [AlarmMailActionDetail] ==============






}