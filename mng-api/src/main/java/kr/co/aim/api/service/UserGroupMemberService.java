package kr.co.aim.api.service;

import kr.co.aim.api.dto.UserGroupMemberCreateRequestDto;
import kr.co.aim.api.dto.UserGroupMemberResponse;
import kr.co.aim.api.dto.UserGroupMemberUpdateRequestDto;
import kr.co.aim.common.condition.UserGroupMemberSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.UserGroupMemberCreateCommand;
import kr.co.aim.domain.command.UserGroupMemberUpdateCommand;
import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMemberHistoryEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class UserGroupMemberService {

    private final UserGroupMemberRepository userGroupMemberRepository;
    private final HistoryService historyService;
    private final UserGroupMemberMapper userGroupMemberMapper;

    /**
     * 조건 및 페이징 기반 사용자-그룹 매핑 목록 조회 (3-Way JOIN 결과 기반, 명시적 for 루프 사용)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupMemberResponse> findUserGroupMembers(UserGroupMemberSearchCondition condition, Pageable pageable) {
        Long userId = null;
        Long userGroupId = null;
        String factoryName = null;

        if (condition != null) {
            userId = condition.getUserId();
            userGroupId = condition.getUserGroupId();
            factoryName = condition.getFactoryName();
        }

        Page<UserGroupMember> pageResult = userGroupMemberRepository.findUserGroupMembersWithDetails(userId, userGroupId, factoryName, pageable);
        List<UserGroupMemberResponse> content = new ArrayList<>();

        for (UserGroupMember member : pageResult.getContent()) {
            if (member != null) {
                // 추가 필터링 조건이 있는 경우 명시적 if 검사
                if (condition != null) {
                    if (condition.getEmployeeId() != null && !condition.getEmployeeId().trim().isEmpty()) {
                        if (member.getEmployeeId() == null || !member.getEmployeeId().toLowerCase().contains(condition.getEmployeeId().trim().toLowerCase())) {
                            continue;
                        }
                    }
                    if (condition.getUserName() != null && !condition.getUserName().trim().isEmpty()) {
                        if (member.getUserName() == null || !member.getUserName().toLowerCase().contains(condition.getUserName().trim().toLowerCase())) {
                            continue;
                        }
                    }
                    if (condition.getUserGroupName() != null && !condition.getUserGroupName().trim().isEmpty()) {
                        if (member.getUserGroupName() == null || !member.getUserGroupName().toLowerCase().contains(condition.getUserGroupName().trim().toLowerCase())) {
                            continue;
                        }
                    }
                }
                content.add(UserGroupMemberResponse.fromDomain(member));
            }
        }

        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 사용자-그룹 매핑 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public UserGroupMemberResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 매핑 ID가 유효하지 않습니다.");
        }
        Optional<UserGroupMember> optionalMember = userGroupMemberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자-그룹 매핑이 존재하지 않습니다. ID: " + id);
        }
        return UserGroupMemberResponse.fromDomain(optionalMember.get());
    }

    /**
     * 특정 사용자의 그룹 매핑 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupMemberResponse> findByUserId(Long userId, Pageable pageable) {
        Page<UserGroupMember> pageResult = userGroupMemberRepository.findUserGroupMembersWithDetails(userId, null, null, pageable);
        List<UserGroupMemberResponse> content = new ArrayList<>();
        for (UserGroupMember member : pageResult.getContent()) {
            if (member != null) {
                content.add(UserGroupMemberResponse.fromDomain(member));
            }
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 특정 사용자 그룹의 소속 사용자 매핑 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupMemberResponse> findByUserGroupId(Long userGroupId, Pageable pageable) {
        Page<UserGroupMember> pageResult = userGroupMemberRepository.findUserGroupMembersWithDetails(null, userGroupId, null, pageable);
        List<UserGroupMemberResponse> content = new ArrayList<>();
        for (UserGroupMember member : pageResult.getContent()) {
            if (member != null) {
                content.add(UserGroupMemberResponse.fromDomain(member));
            }
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 신규 사용자-그룹 매핑 등록 (CREATE)
     * - 중복 검증 (userGroupId + userId)
     * - UserGroupMember.create(command) 도메인 팩토리 메서드 활용 (TSID 내부 자동 발급)
     * - USER_GROUP_MEMBER_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupMemberResponse createUserGroupMember(UserGroupMemberCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("사용자-그룹 매핑 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("사용자 ID(userId)는 필수 입력 항목입니다.");
        }
        if (dto.getUserGroupId() == null) {
            throw new IllegalArgumentException("사용자 그룹 ID(userGroupId)는 필수 입력 항목입니다.");
        }

        // 1. 중복 검증 (동일 그룹에 동일 사용자 중복 매핑 방지)
        Optional<UserGroupMember> existing = userGroupMemberRepository.findByUserGroupIdAndUserId(dto.getUserGroupId(), dto.getUserId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("해당 사용자는 이미 이 그룹에 배정되어 있습니다. (UserId: " + dto.getUserId() + ", UserGroupId: " + dto.getUserGroupId() + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupMemberCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group member created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 도메인 커맨드 객체 구성 및 엔티티 생성 (도메인 모델 내부 팩토리 호출)
        UserGroupMemberCreateCommand command = UserGroupMemberCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName().trim())
                .userId(dto.getUserId())
                .userGroupId(dto.getUserGroupId())
                .build();

        UserGroupMember member = UserGroupMember.create(command);

        // 4. USER_GROUP_MEMBER 저장
        UserGroupMember savedMember = userGroupMemberRepository.save(member);

        // 5. 동일 트랜잭션 내 USER_GROUP_MEMBER_HISTORY 자동 적재
        UserGroupMemberHistoryEntity historyEntity = userGroupMemberMapper.toHistoryEntity(savedMember);
        historyService.saveHistory(historyEntity);

        log.info("UserGroupMember created successfully: [id={}, userId={}, userGroupId={}]", savedMember.getId(), savedMember.getUserId(), savedMember.getUserGroupId());

        return UserGroupMemberResponse.fromDomain(savedMember);
    }

    /**
     * TSID(ID) 기반 사용자-그룹 매핑 정보 수정 (UPDATE)
     * - 대상 매핑 존재 검증
     * - 그룹 또는 사용자 변경 시 중복 검증
     * - member.update(command) 도메인 비즈니스 메서드 활용
     * - USER_GROUP_MEMBER_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupMemberResponse updateUserGroupMember(Long id, UserGroupMemberUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 매핑 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 매핑 정보가 유효하지 않습니다.");
        }

        // 1. 대상 매핑 조회
        Optional<UserGroupMember> optionalMember = userGroupMemberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 사용자-그룹 매핑이 존재하지 않습니다. ID: " + id);
        }

        UserGroupMember member = optionalMember.get();

        Long targetUserId = dto.getUserId() != null ? dto.getUserId() : member.getUserId();
        Long targetGroupId = dto.getUserGroupId() != null ? dto.getUserGroupId() : member.getUserGroupId();

        // 2. 그룹 또는 사용자 변경 시 중복 검증
        if (!targetUserId.equals(member.getUserId()) || !targetGroupId.equals(member.getUserGroupId())) {
            Optional<UserGroupMember> duplicate = userGroupMemberRepository.findByUserGroupIdAndUserId(targetGroupId, targetUserId);
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("해당 사용자는 이미 이 그룹에 배정되어 있습니다. (UserId: " + targetUserId + ", UserGroupId: " + targetGroupId + ")");
            }
        }

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupMemberModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group member modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 객체 구성 및 수정 적용 (도메인 모델 내부 메서드 호출)
        UserGroupMemberUpdateCommand command = UserGroupMemberUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName() != null ? dto.getFactoryName().trim() : null)
                .userId(dto.getUserId())
                .userGroupId(dto.getUserGroupId())
                .build();

        member.update(command);

        // 5. USER_GROUP_MEMBER 저장
        UserGroupMember updatedMember = userGroupMemberRepository.save(member);

        // 6. 동일 트랜잭션 내 USER_GROUP_MEMBER_HISTORY 자동 적재
        UserGroupMemberHistoryEntity historyEntity = userGroupMemberMapper.toHistoryEntity(updatedMember);
        historyService.saveHistory(historyEntity);

        log.info("UserGroupMember updated successfully: [id={}, userId={}, userGroupId={}]", updatedMember.getId(), updatedMember.getUserId(), updatedMember.getUserGroupId());

        return UserGroupMemberResponse.fromDomain(updatedMember);
    }

    /**
     * TSID(ID) 기반 단건 사용자-그룹 매핑 삭제 (DELETE)
     * - 대상 조회
     * - USER_GROUP_MEMBER_HISTORY에 삭제 이력 적재
     * - USER_GROUP_MEMBER에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroupMember(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<UserGroupMember> optionalMember = userGroupMemberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 사용자-그룹 매핑이 존재하지 않습니다. ID: " + id);
        }

        UserGroupMember member = optionalMember.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        member.setEventName("UserGroupMemberDeleted");
        member.setEventTime(now);
        member.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        member.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "User group member deleted");

        // 2. 삭제 이력 적재
        UserGroupMemberHistoryEntity historyEntity = userGroupMemberMapper.toHistoryEntity(member);
        historyService.saveHistory(historyEntity);

        // 3. 매핑 삭제 (TSID 기준)
        userGroupMemberRepository.deleteById(id);

        log.info("UserGroupMember deleted successfully: [id={}, userId={}, userGroupId={}]", id, member.getUserId(), member.getUserGroupId());
    }

    /**
     * TSID(ID) 목록 기반 복수 사용자-그룹 매핑 벌크 삭제 (DELETE batch)
     * - 각 대상에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroupMembers(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch user group member deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<UserGroupMember> optionalMember = userGroupMemberRepository.findById(id);
            if (optionalMember.isPresent()) {
                UserGroupMember member = optionalMember.get();
                member.setEventName("UserGroupMemberDeleted");
                member.setEventTime(now);
                member.setEventUser(user);
                member.setEventComment(comment);

                UserGroupMemberHistoryEntity historyEntity = userGroupMemberMapper.toHistoryEntity(member);
                historyService.saveHistory(historyEntity);
            }
        }

        userGroupMemberRepository.deleteAllByIdInBatch(ids);
        log.info("UserGroupMembers batch deleted successfully: [count={}]", ids.size());
    }
}
