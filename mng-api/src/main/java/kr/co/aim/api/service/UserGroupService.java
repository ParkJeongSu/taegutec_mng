package kr.co.aim.api.service;

import kr.co.aim.api.dto.UserGroupCreateRequestDto;
import kr.co.aim.api.dto.UserGroupResponse;
import kr.co.aim.api.dto.UserGroupUpdateRequestDto;
import kr.co.aim.common.condition.UserGroupSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.UserGroupCreateCommand;
import kr.co.aim.domain.command.UserGroupUpdateCommand;
import kr.co.aim.domain.model.UserGroup;
import kr.co.aim.domain.repository.UserGroupRepository;
import kr.co.aim.infra.persistence.entity.UserGroupHistoryEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final HistoryService historyService;
    private final UserGroupMapper userGroupMapper;

    /**
     * 조건 및 페이징 기반 사용자 그룹 목록 조회 (명시적 for 루프 및 if 필터링)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupResponse> findUserGroups(UserGroupSearchCondition condition, Pageable pageable) {
        List<UserGroup> allGroups = userGroupRepository.findAll();
        List<UserGroupResponse> filteredList = new ArrayList<>();

        for (UserGroup group : allGroups) {
            if (group == null) {
                continue;
            }

            if (condition != null) {
                if (condition.getFactoryName() != null && !condition.getFactoryName().trim().isEmpty()) {
                    if (group.getFactoryName() == null || !group.getFactoryName().equalsIgnoreCase(condition.getFactoryName().trim())) {
                        continue;
                    }
                }
                if (condition.getUserGroupName() != null && !condition.getUserGroupName().trim().isEmpty()) {
                    if (group.getUserGroupName() == null || !group.getUserGroupName().toLowerCase().contains(condition.getUserGroupName().trim().toLowerCase())) {
                        continue;
                    }
                }
                if (condition.getDescription() != null && !condition.getDescription().trim().isEmpty()) {
                    if (group.getDescription() == null || !group.getDescription().toLowerCase().contains(condition.getDescription().trim().toLowerCase())) {
                        continue;
                    }
                }
                if (condition.getUseState() != null && !condition.getUseState().trim().isEmpty()) {
                    if (group.getUseState() == null || !condition.getUseState().trim().equalsIgnoreCase(group.getUseState())) {
                        continue;
                    }
                }
            }

            filteredList.add(UserGroupResponse.fromDomain(group));
        }

        if (pageable == null || pageable.isUnpaged()) {
            return new PageImpl<>(filteredList, PageRequest.of(0, filteredList.isEmpty() ? 1 : filteredList.size()), filteredList.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());

        List<UserGroupResponse> pageContent = new ArrayList<>();
        if (start <= filteredList.size()) {
            pageContent = filteredList.subList(start, end);
        }

        return new PageImpl<>(pageContent, pageable, filteredList.size());
    }

    /**
     * TSID(ID) 기반 사용자 그룹 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public UserGroupResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 사용자 그룹 ID가 유효하지 않습니다.");
        }
        Optional<UserGroup> optionalGroup = userGroupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자 그룹이 존재하지 않습니다. ID: " + id);
        }
        return UserGroupResponse.fromDomain(optionalGroup.get());
    }

    /**
     * 신규 사용자 그룹 등록 (CREATE)
     * - 중복 검증 (factoryName + userGroupName)
     * - UserGroup.create(command) 도메인 팩토리 메서드 활용 (TSID 내부 자동 발급)
     * - USER_GROUP_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupResponse createUserGroup(UserGroupCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("사용자 그룹 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getUserGroupName() == null || dto.getUserGroupName().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 그룹명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String userGroupName = dto.getUserGroupName().trim();

        // 1. 중복 검증
        Optional<UserGroup> existing = userGroupRepository.findByFactoryNameAndUserGroupName(factoryName, userGroupName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 그룹명입니다: " + userGroupName + " (공장: " + factoryName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 도메인 커맨드 객체 구성 및 엔티티 생성 (도메인 모델 내부 팩토리 호출)
        UserGroupCreateCommand command = UserGroupCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .userGroupName(userGroupName)
                .description(dto.getDescription())
                .useState(dto.getUseState() != null && !dto.getUseState().trim().isEmpty() ? dto.getUseState().trim() : "ACTIVE")
                .build();

        UserGroup userGroup = UserGroup.create(command);

        // 4. USER_GROUP 저장
        UserGroup savedGroup = userGroupRepository.save(userGroup);

        // 5. 동일 트랜잭션 내 USER_GROUP_HISTORY 자동 적재
        UserGroupHistoryEntity historyEntity = userGroupMapper.toHistoryEntity(savedGroup);
        historyService.saveHistory(historyEntity);

        log.info("UserGroup created successfully: [id={}, userGroupName={}, factoryName={}]", savedGroup.getId(), savedGroup.getUserGroupName(), savedGroup.getFactoryName());

        return UserGroupResponse.fromDomain(savedGroup);
    }

    /**
     * TSID(ID) 기반 사용자 그룹 정보 수정 (UPDATE)
     * - 대상 사용자 그룹 존재 검증
     * - 그룹명 변경 시 중복 검증
     * - userGroup.update(command) 도메인 비즈니스 메서드 활용
     * - USER_GROUP_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupResponse updateUserGroup(Long id, UserGroupUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 사용자 그룹 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 사용자 그룹 정보가 유효하지 않습니다.");
        }

        // 1. 대상 사용자 그룹 조회
        Optional<UserGroup> optionalGroup = userGroupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 사용자 그룹이 존재하지 않습니다. ID: " + id);
        }

        UserGroup userGroup = optionalGroup.get();

        // 2. 그룹명 또는 공장 변경 시 중복 검증
        if (dto.getUserGroupName() != null && !dto.getUserGroupName().trim().isEmpty()) {
            String targetFactory = (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty())
                    ? dto.getFactoryName().trim()
                    : userGroup.getFactoryName();
            String targetGroupName = dto.getUserGroupName().trim();

            if (!targetGroupName.equals(userGroup.getUserGroupName()) || (targetFactory != null && !targetFactory.equals(userGroup.getFactoryName()))) {
                Optional<UserGroup> duplicate = userGroupRepository.findByFactoryNameAndUserGroupName(targetFactory, targetGroupName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 사용자 그룹명입니다: " + targetGroupName + " (공장: " + targetFactory + ")");
                }
            }
        }

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 객체 구성 및 수정 적용 (도메인 모델 내부 메서드 호출)
        UserGroupUpdateCommand command = UserGroupUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName() != null ? dto.getFactoryName().trim() : null)
                .userGroupName(dto.getUserGroupName() != null ? dto.getUserGroupName().trim() : null)
                .description(dto.getDescription())
                .useState(dto.getUseState() != null ? dto.getUseState().trim() : null)
                .build();

        userGroup.update(command);

        // 5. USER_GROUP 저장
        UserGroup updatedGroup = userGroupRepository.save(userGroup);

        // 6. 동일 트랜잭션 내 USER_GROUP_HISTORY 자동 적재
        UserGroupHistoryEntity historyEntity = userGroupMapper.toHistoryEntity(updatedGroup);
        historyService.saveHistory(historyEntity);

        log.info("UserGroup updated successfully: [id={}, userGroupName={}]", updatedGroup.getId(), updatedGroup.getUserGroupName());

        return UserGroupResponse.fromDomain(updatedGroup);
    }

    /**
     * TSID(ID) 기반 단건 사용자 그룹 삭제 (DELETE)
     * - 대상 조회
     * - USER_GROUP_HISTORY에 삭제 이력 적재
     * - USER_GROUP에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroup(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<UserGroup> optionalGroup = userGroupRepository.findById(id);
        if (optionalGroup.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 사용자 그룹이 존재하지 않습니다. ID: " + id);
        }

        UserGroup userGroup = optionalGroup.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        userGroup.setEventName("UserGroupDeleted");
        userGroup.setEventTime(now);
        userGroup.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        userGroup.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "User group deleted");

        // 2. 삭제 이력 적재
        UserGroupHistoryEntity historyEntity = userGroupMapper.toHistoryEntity(userGroup);
        historyService.saveHistory(historyEntity);

        // 3. 사용자 그룹 삭제 (TSID 기준)
        userGroupRepository.deleteById(id);

        log.info("UserGroup deleted successfully: [id={}, userGroupName={}]", id, userGroup.getUserGroupName());
    }

    /**
     * TSID(ID) 목록 기반 복수 사용자 그룹 벌크 삭제 (DELETE batch)
     * - 각 대상에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroups(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch user group deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<UserGroup> optionalGroup = userGroupRepository.findById(id);
            if (optionalGroup.isPresent()) {
                UserGroup userGroup = optionalGroup.get();
                userGroup.setEventName("UserGroupDeleted");
                userGroup.setEventTime(now);
                userGroup.setEventUser(user);
                userGroup.setEventComment(comment);

                UserGroupHistoryEntity historyEntity = userGroupMapper.toHistoryEntity(userGroup);
                historyService.saveHistory(historyEntity);
            }
        }

        userGroupRepository.deleteAllByIdInBatch(ids);
        log.info("UserGroups batch deleted successfully: [count={}]", ids.size());
    }
}
