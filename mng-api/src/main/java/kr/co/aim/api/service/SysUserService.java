package kr.co.aim.api.service;

import kr.co.aim.api.dto.SysUserCreateRequestDto;
import kr.co.aim.api.dto.SysUserResponse;
import kr.co.aim.api.dto.SysUserUpdateRequestDto;
import kr.co.aim.api.dto.UserGroupMemberResponse;
import kr.co.aim.common.condition.SysUserSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.SysUserCreateCommand;
import kr.co.aim.domain.command.SysUserUpdateCommand;
import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.SysUserHistoryEntity;
import kr.co.aim.infra.persistence.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserService {

    private final UserRepository userRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final HistoryService historyService;
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 조건 및 페이징 기반 사용자 목록 조회 (부서명 조인 포함)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<SysUserResponse> findUsers(SysUserSearchCondition condition, Pageable pageable) {
        Page<SysUser> pageResult = userRepository.findUserWithConditions(condition, pageable);
        List<SysUserResponse> content = new ArrayList<>();
        for (SysUser user : pageResult.getContent()) {
            content.add(SysUserResponse.fromDomain(user));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 사용자 단건 상세 조회 (부서명 조인 포함)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public SysUserResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 사용자 ID가 유효하지 않습니다.");
        }
        Optional<SysUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID: " + id);
        }
        return SysUserResponse.fromDomain(optionalUser.get());
    }

    /**
     * 사용자-그룹 매핑 목록 조회 (3-Way JOIN: USER_GROUP_MEMBER, SYS_USER, USER_GROUP)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupMemberResponse> findUserGroupMembers(Long userId, Long userGroupId, String factoryName, Pageable pageable) {
        Page<UserGroupMember> pageResult = userGroupMemberRepository.findUserGroupMembersWithDetails(userId, userGroupId, factoryName, pageable);
        List<UserGroupMemberResponse> content = new ArrayList<>();
        for (UserGroupMember member : pageResult.getContent()) {
            content.add(UserGroupMemberResponse.fromDomain(member));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * 신규 사용자 등록 (CREATE)
     * - 중복 검증 (factoryName + userId)
     * - 비밀번호 암호화 (PasswordEncoder.encode)
     * - TSID 사전 발급
     * - SYS_USER_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public SysUserResponse createUser(SysUserCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("사용자 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getUserId() == null || dto.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수 입력 항목입니다.");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 항목입니다.");
        }
        if (dto.getUserName() == null || dto.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String userId = dto.getUserId().trim();

        // 1. 중복 검증
        Optional<SysUser> existing = userRepository.findByFactoryNameAndUserId(factoryName, userId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다: " + userId + " (공장: " + factoryName + ")");
        }

        // 2. 비밀번호 단방향 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword().trim());

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 객체 구성 및 엔티티 생성 (TSID 내부 자동 발급)
        SysUserCreateCommand command = SysUserCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .userId(userId)
                .passwordHash(encodedPassword)
                .userName(dto.getUserName().trim())
                .departmentId(dto.getDepartmentId())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .userState(dto.getUserState() != null ? dto.getUserState().trim() : "ACTIVE")
                .build();

        SysUser sysUser = SysUser.create(command);

        // 5. SYS_USER 저장
        SysUser savedUser = userRepository.save(sysUser);

        // 6. 동일 트랜잭션 내 SYS_USER_HISTORY 자동 적재
        SysUserHistoryEntity historyEntity = sysUserMapper.toHistoryEntity(savedUser);
        historyService.saveHistory(historyEntity);

        log.info("SysUser created successfully: [id={}, userId={}, factoryName={}]", savedUser.getId(), savedUser.getUserId(), savedUser.getFactoryName());

        // 부서 정보 포함 조회
        Optional<SysUser> reloaded = userRepository.findById(savedUser.getId());
        if (reloaded.isPresent()) {
            return SysUserResponse.fromDomain(reloaded.get());
        }
        return SysUserResponse.fromDomain(savedUser);
    }

    /**
     * TSID(ID) 기반 사용자 정보 수정 (UPDATE)
     * - 대상 사용자 존재 검증
     * - 비밀번호 변경 요청 시 PasswordEncoder.encode() 적용 및 passwordChangeTime 갱신
     * - SYS_USER_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public SysUserResponse updateUser(Long id, SysUserUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 사용자 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 사용자 정보가 유효하지 않습니다.");
        }

        // 1. 대상 사용자 조회
        Optional<SysUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 사용자가 존재하지 않습니다. ID: " + id);
        }

        SysUser sysUser = optionalUser.get();

        // 2. 비밀번호 변경 여부 확인 및 암호화
        String encodedPassword = null;
        LocalDateTime passwordChangeTime = null;
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            encodedPassword = passwordEncoder.encode(dto.getPassword().trim());
            passwordChangeTime = LocalDateTime.now();
        }

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 객체 구성 및 수정 적용
        SysUserUpdateCommand command = SysUserUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName())
                .passwordHash(encodedPassword)
                .passwordChangeTime(passwordChangeTime)
                .userName(dto.getUserName())
                .departmentId(dto.getDepartmentId())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .userState(dto.getUserState())
                .failedLoginCount(dto.getFailedLoginCount())
                .build();

        sysUser.update(command);

        // 5. SYS_USER 저장
        SysUser updatedUser = userRepository.save(sysUser);

        // 6. 동일 트랜잭션 내 SYS_USER_HISTORY 자동 적재
        SysUserHistoryEntity historyEntity = sysUserMapper.toHistoryEntity(updatedUser);
        historyService.saveHistory(historyEntity);

        log.info("SysUser updated successfully: [id={}, userId={}]", updatedUser.getId(), updatedUser.getUserId());

        // 부서 정보 포함 재조회
        Optional<SysUser> reloaded = userRepository.findById(updatedUser.getId());
        if (reloaded.isPresent()) {
            return SysUserResponse.fromDomain(reloaded.get());
        }
        return SysUserResponse.fromDomain(updatedUser);
    }

    /**
     * TSID(ID) 기반 단건 사용자 삭제 (DELETE)
     * - 대상 사용자 조회
     * - SYS_USER_HISTORY에 삭제 이력 적재
     * - SYS_USER에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUser(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<SysUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 사용자가 존재하지 않습니다. ID: " + id);
        }

        SysUser sysUser = optionalUser.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        sysUser.setEventName("UserDeleted");
        sysUser.setEventTime(now);
        sysUser.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        sysUser.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "User deleted");

        // 2. 삭제 이력 적재
        SysUserHistoryEntity historyEntity = sysUserMapper.toHistoryEntity(sysUser);
        historyService.saveHistory(historyEntity);

        // 3. 사용자 삭제 (TSID 기준)
        userRepository.deleteById(id);

        log.info("SysUser deleted successfully: [id={}, userId={}]", id, sysUser.getUserId());
    }

    /**
     * TSID(ID) 목록 기반 복수 사용자 벌크 삭제 (DELETE batch)
     * - 각 대상 사용자에 대한 삭제 이력 자동 적재
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUsers(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch user deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<SysUser> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                SysUser sysUser = optionalUser.get();
                sysUser.setEventName("UserDeleted");
                sysUser.setEventTime(now);
                sysUser.setEventUser(user);
                sysUser.setEventComment(comment);

                SysUserHistoryEntity historyEntity = sysUserMapper.toHistoryEntity(sysUser);
                historyService.saveHistory(historyEntity);
            }
        }

        userRepository.deleteAllByIdInBatch(ids);
        log.info("SysUsers batch deleted successfully: [count={}]", ids.size());
    }
}

