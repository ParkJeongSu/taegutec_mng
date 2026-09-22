package kr.co.aim.api.service;

import kr.co.aim.api.dto.PasswordPolicyCreateRequestDto;
import kr.co.aim.api.dto.PasswordPolicyResponse;
import kr.co.aim.api.dto.PasswordPolicyUpdateRequestDto;
import kr.co.aim.common.condition.PasswordPolicySearchCondition;
import kr.co.aim.common.enums.PasswordPolicyType;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.PasswordPolicyCreateCommand;
import kr.co.aim.domain.command.PasswordPolicyUpdateCommand;
import kr.co.aim.domain.model.PasswordPolicy;
import kr.co.aim.domain.repository.PasswordPolicyRepository;
import kr.co.aim.infra.persistence.entity.PasswordPolicyHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PasswordPolicyMapper;
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
public class PasswordPolicyService {

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':\"<>?,./";

    private final PasswordPolicyRepository passwordPolicyRepository;
    private final HistoryService historyService;
    private final PasswordPolicyMapper passwordPolicyMapper;

    /**
     * 비밀번호 복잡도 정책 유효성 검증 (10대 Password Rules 기반)
     * - Stream/Lambda를 배제하고 명시적 for 루프 및 if 분기문으로만 검증 수행
     * - 정책 위반 시 구체적인 한글 안내 메시지와 함께 IllegalArgumentException 발생
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public void validatePasswordRules(String factoryName, String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호를 입력해주세요.");
        }

        // 1. 기본 정책 규칙 기준값 설정 (Enum defaultValue 기반)
        int minCharacters = Integer.parseInt(PasswordPolicyType.MIN_CHARACTERS.getDefaultValue());
        int minUpperCase = Integer.parseInt(PasswordPolicyType.MIN_UPPER_CASE.getDefaultValue());
        int minLowerCase = Integer.parseInt(PasswordPolicyType.MIN_LOWER_CASE.getDefaultValue());
        int minNumeric = Integer.parseInt(PasswordPolicyType.MIN_NUMERIC.getDefaultValue());
        int minSpecialCharacters = Integer.parseInt(PasswordPolicyType.MIN_SPECIAL_CHARACTERS.getDefaultValue());
        int maxIdenticalConsecutive = Integer.parseInt(PasswordPolicyType.MAX_IDENTICAL_CONSECUTIVE.getDefaultValue());

        // 2. 공장별 활성화된 패스워드 정책이 DB에 존재하는지 확인
        if (factoryName != null && !factoryName.trim().isEmpty()) {
            Optional<PasswordPolicy> policyOptional = passwordPolicyRepository.findByFactoryName(factoryName.trim());
            if (policyOptional.isPresent()) {
                PasswordPolicy policy = policyOptional.get();
                // 정책이 활성화("Y") 상태가 아니면 기본 정책을 그대로 사용하거나 통과할 수 있음
                if ("N".equalsIgnoreCase(policy.getIsActive())) {
                    log.info("Password policy is inactive for factory: {}. Skipping complexity validation.", factoryName);
                    return;
                }
            }
        }

        // 3. 문자열 검사 및 문자 유형별 카운트 집계 (명시적 for 루프 사용)
        char[] chars = rawPassword.toCharArray();
        int upperCaseCount = 0;
        int lowerCaseCount = 0;
        int numericCount = 0;
        int specialCharCount = 0;

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                upperCaseCount++;
            } else if (ch >= 'a' && ch <= 'z') {
                lowerCaseCount++;
            } else if (ch >= '0' && ch <= '9') {
                numericCount++;
            } else if (SPECIAL_CHARACTERS.indexOf(ch) >= 0) {
                specialCharCount++;
            }
        }

        // 4. 규칙 검증 1: 최소 길이 (MIN_CHARACTERS)
        if (chars.length < minCharacters) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호는 최소 " + minCharacters + "자 이상이어야 합니다. (현재 길이: " + chars.length + ")");
        }

        // 5. 규칙 검증 2: 영문 대문자 최소 개수 (MIN_UPPER_CASE)
        if (upperCaseCount < minUpperCase) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호에는 영문 대문자(A-Z)가 최소 " + minUpperCase + "개 이상 포함되어야 합니다. (현재 개수: " + upperCaseCount + ")");
        }

        // 6. 규칙 검증 3: 영문 소문자 최소 개수 (MIN_LOWER_CASE)
        if (lowerCaseCount < minLowerCase) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호에는 영문 소문자(a-z)가 최소 " + minLowerCase + "개 이상 포함되어야 합니다. (현재 개수: " + lowerCaseCount + ")");
        }

        // 7. 규칙 검증 4: 숫자 최소 개수 (MIN_NUMERIC)
        if (numericCount < minNumeric) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호에는 숫자(0-9)가 최소 " + minNumeric + "개 이상 포함되어야 합니다. (현재 개수: " + numericCount + ")");
        }

        // 8. 규칙 검증 5: 특수문자 최소 개수 (MIN_SPECIAL_CHARACTERS)
        if (minSpecialCharacters > 0 && specialCharCount < minSpecialCharacters) {
            throw new IllegalArgumentException("[비밀번호 정책 위반] 비밀번호에는 특수문자(" + SPECIAL_CHARACTERS + ")가 최소 " + minSpecialCharacters + "개 이상 포함되어야 합니다. (현재 개수: " + specialCharCount + ")");
        }

        // 9. 규칙 검증 6: 동일 문자 연속 반복 최대 허용 (MAX_IDENTICAL_CONSECUTIVE)
        // 최대 허용이 2인 경우 3회 이상 동일 문자 연속 시 위반
        if (maxIdenticalConsecutive > 0 && chars.length > maxIdenticalConsecutive) {
            int consecutiveCount = 1;
            for (int i = 1; i < chars.length; i++) {
                if (chars[i] == chars[i - 1]) {
                    consecutiveCount++;
                    if (consecutiveCount > maxIdenticalConsecutive) {
                        throw new IllegalArgumentException("[비밀번호 정책 위반] 동일한 문자를 연속으로 " + (maxIdenticalConsecutive + 1) + "회 이상 반복 사용할 수 없습니다. (반복 문자: '" + chars[i] + "')");
                    }
                } else {
                    consecutiveCount = 1;
                }
            }
        }
    }

    /**
     * 조건 및 페이징 기반 패스워드 정책 목록 조회 (명시적 for 루프 및 if 분기문 필터링)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<PasswordPolicyResponse> findPasswordPolicies(PasswordPolicySearchCondition condition, Pageable pageable) {
        List<PasswordPolicy> allPolicies = passwordPolicyRepository.findAll();
        List<PasswordPolicyResponse> filteredList = new ArrayList<>();

        for (PasswordPolicy policy : allPolicies) {
            if (policy == null) {
                continue;
            }

            if (condition != null) {
                if (condition.getFactoryName() != null && !condition.getFactoryName().trim().isEmpty()) {
                    if (policy.getFactoryName() == null || !policy.getFactoryName().equalsIgnoreCase(condition.getFactoryName().trim())) {
                        continue;
                    }
                }
                if (condition.getPolicyName() != null && !condition.getPolicyName().trim().isEmpty()) {
                    if (policy.getPolicyName() == null || !policy.getPolicyName().toLowerCase().contains(condition.getPolicyName().trim().toLowerCase())) {
                        continue;
                    }
                }
                if (condition.getIsActive() != null && !condition.getIsActive().trim().isEmpty()) {
                    if (policy.getIsActive() == null || !policy.getIsActive().equalsIgnoreCase(condition.getIsActive().trim())) {
                        continue;
                    }
                }
            }

            filteredList.add(PasswordPolicyResponse.fromDomain(policy));
        }

        if (pageable == null || pageable.isUnpaged()) {
            int size = filteredList.isEmpty() ? 1 : filteredList.size();
            return new PageImpl<>(filteredList, PageRequest.of(0, size), filteredList.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());

        List<PasswordPolicyResponse> pageContent = new ArrayList<>();
        if (start <= filteredList.size()) {
            pageContent = filteredList.subList(start, end);
        }

        return new PageImpl<>(pageContent, pageable, filteredList.size());
    }

    /**
     * TSID(ID) 기반 패스워드 정책 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public PasswordPolicyResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 정책 ID가 유효하지 않습니다.");
        }

        Optional<PasswordPolicy> optionalPolicy = passwordPolicyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            throw new IllegalArgumentException("해당 패스워드 정책이 존재하지 않습니다. ID: " + id);
        }

        return PasswordPolicyResponse.fromDomain(optionalPolicy.get());
    }

    /**
     * 공장 구분 기준 패스워드 정책 단건 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public PasswordPolicyResponse findByFactoryName(String factoryName) {
        if (factoryName == null || factoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("조회할 공장 구분이 유효하지 않습니다.");
        }

        Optional<PasswordPolicy> optionalPolicy = passwordPolicyRepository.findByFactoryName(factoryName.trim());
        if (optionalPolicy.isEmpty()) {
            throw new IllegalArgumentException("해당 공장의 패스워드 정책이 존재하지 않습니다. Factory: " + factoryName);
        }

        return PasswordPolicyResponse.fromDomain(optionalPolicy.get());
    }

    /**
     * 신규 패스워드 정책 등록 (CREATE)
     * - 중복 검증 (factoryName + policyName)
     * - 도메인 내부 PasswordPolicy.create(command) 호출 (TSID 자동 채번)
     * - PASSWORD_POLICY_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public PasswordPolicyResponse createPasswordPolicy(PasswordPolicyCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("패스워드 정책 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getPolicyName() == null || dto.getPolicyName().trim().isEmpty()) {
            throw new IllegalArgumentException("정책명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String policyName = dto.getPolicyName().trim();

        // 1. 중복 검증 (동일 공장에 동일 정책명 중복 등록 방지)
        Optional<PasswordPolicy> existing = passwordPolicyRepository.findByFactoryNameAndPolicyName(factoryName, policyName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 패스워드 정책명입니다: " + policyName + " (공장: " + factoryName + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "PasswordPolicyCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Password policy created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 도메인 커맨드 생성 및 도메인 모델 생성 (도메인 모델 팩토리 메서드 활용)
        PasswordPolicyCreateCommand command = PasswordPolicyCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .policyName(policyName)
                .isActive(dto.getIsActive() != null && !dto.getIsActive().trim().isEmpty() ? dto.getIsActive().trim() : "Y")
                .build();

        PasswordPolicy policy = PasswordPolicy.create(command);

        // 4. PASSWORD_POLICY 저장
        PasswordPolicy savedPolicy = passwordPolicyRepository.save(policy);

        // 5. 동일 트랜잭션 내 PASSWORD_POLICY_HISTORY 자동 적재
        PasswordPolicyHistoryEntity historyEntity = passwordPolicyMapper.toHistoryEntity(savedPolicy);
        historyService.saveHistory(historyEntity);

        log.info("PasswordPolicy created successfully: [id={}, factoryName={}, policyName={}, isActive={}]",
                savedPolicy.getId(), savedPolicy.getFactoryName(), savedPolicy.getPolicyName(), savedPolicy.getIsActive());

        return PasswordPolicyResponse.fromDomain(savedPolicy);
    }

    /**
     * TSID(ID) 기반 패스워드 정책 수정 (UPDATE)
     * - 대상 존재 검증
     * - 정책명 변경 시 중복 검증
     * - 도메인 내부 policy.update(command) 호출
     * - PASSWORD_POLICY_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public PasswordPolicyResponse updatePasswordPolicy(Long id, PasswordPolicyUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 정책 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 정책 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<PasswordPolicy> optionalPolicy = passwordPolicyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 패스워드 정책이 존재하지 않습니다. ID: " + id);
        }

        PasswordPolicy policy = optionalPolicy.get();

        // 2. 정책명 변경 시 중복 검증
        if (dto.getPolicyName() != null && !dto.getPolicyName().trim().isEmpty()) {
            String targetPolicyName = dto.getPolicyName().trim();
            if (!targetPolicyName.equals(policy.getPolicyName())) {
                Optional<PasswordPolicy> duplicate = passwordPolicyRepository.findByFactoryNameAndPolicyName(policy.getFactoryName(), targetPolicyName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 패스워드 정책명입니다: " + targetPolicyName + " (공장: " + policy.getFactoryName() + ")");
                }
            }
        }

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "PasswordPolicyModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Password policy modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 생성 및 도메인 모델 수정 (도메인 모델 비즈니스 메서드 활용)
        PasswordPolicyUpdateCommand command = PasswordPolicyUpdateCommand.builder()
                .transactionInfo(tx)
                .policyName(dto.getPolicyName() != null ? dto.getPolicyName().trim() : null)
                .isActive(dto.getIsActive() != null ? dto.getIsActive().trim() : null)
                .build();

        policy.update(command);

        // 5. PASSWORD_POLICY 저장
        PasswordPolicy updatedPolicy = passwordPolicyRepository.save(policy);

        // 6. 동일 트랜잭션 내 PASSWORD_POLICY_HISTORY 자동 적재
        PasswordPolicyHistoryEntity historyEntity = passwordPolicyMapper.toHistoryEntity(updatedPolicy);
        historyService.saveHistory(historyEntity);

        log.info("PasswordPolicy updated successfully: [id={}, factoryName={}, policyName={}, isActive={}]",
                updatedPolicy.getId(), updatedPolicy.getFactoryName(), updatedPolicy.getPolicyName(), updatedPolicy.getIsActive());

        return PasswordPolicyResponse.fromDomain(updatedPolicy);
    }

    /**
     * TSID(ID) 기반 단건 패스워드 정책 삭제 (DELETE)
     * - 대상 조회
     * - PASSWORD_POLICY_HISTORY에 삭제 이력 적재
     * - PASSWORD_POLICY에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deletePasswordPolicy(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<PasswordPolicy> optionalPolicy = passwordPolicyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 패스워드 정책이 존재하지 않습니다. ID: " + id);
        }

        PasswordPolicy policy = optionalPolicy.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        policy.setEventName("PasswordPolicyDeleted");
        policy.setEventTime(now);
        policy.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        policy.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Password policy deleted");

        // 2. 삭제 이력 적재
        PasswordPolicyHistoryEntity historyEntity = passwordPolicyMapper.toHistoryEntity(policy);
        historyService.saveHistory(historyEntity);

        // 3. 패스워드 정책 삭제 (TSID 기준)
        passwordPolicyRepository.deleteById(id);

        log.info("PasswordPolicy deleted successfully: [id={}, factoryName={}, policyName={}]", id, policy.getFactoryName(), policy.getPolicyName());
    }

    /**
     * TSID(ID) 목록 기반 복수 패스워드 정책 벌크 삭제 (DELETE batch)
     * - 각 대상에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deletePasswordPolicies(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch password policies deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<PasswordPolicy> optionalPolicy = passwordPolicyRepository.findById(id);
            if (optionalPolicy.isPresent()) {
                PasswordPolicy policy = optionalPolicy.get();
                policy.setEventName("PasswordPolicyDeleted");
                policy.setEventTime(now);
                policy.setEventUser(user);
                policy.setEventComment(comment);

                PasswordPolicyHistoryEntity historyEntity = passwordPolicyMapper.toHistoryEntity(policy);
                historyService.saveHistory(historyEntity);
            }
        }

        passwordPolicyRepository.deleteAllByIdInBatch(ids);
        log.info("PasswordPolicies batch deleted successfully: [count={}]", ids.size());
    }
}
