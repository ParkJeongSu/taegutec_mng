package kr.co.aim.api.service;

import kr.co.aim.api.dto.PasswordPolicyCreateRequestDto;
import kr.co.aim.api.dto.PasswordPolicyResponse;
import kr.co.aim.api.dto.PasswordPolicyUpdateRequestDto;
import kr.co.aim.common.condition.PasswordPolicySearchCondition;
import kr.co.aim.domain.model.PasswordPolicy;
import kr.co.aim.domain.repository.PasswordPolicyRepository;
import kr.co.aim.infra.persistence.entity.PasswordPolicyHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PasswordPolicyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyServiceTest {

    @Mock
    private PasswordPolicyRepository passwordPolicyRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private PasswordPolicyMapper passwordPolicyMapper;

    @InjectMocks
    private PasswordPolicyService passwordPolicyService;

    private PasswordPolicy samplePolicy;

    @BeforeEach
    void setUp() {
        samplePolicy = PasswordPolicy.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .policyName("DEFAULT_POLICY")
                .isActive("Y")
                .eventName("PasswordPolicyCreated")
                .eventTime(LocalDateTime.now())
                .eventUser("SYSTEM")
                .eventComment("Initial password policy creation")
                .build();
    }

    @Test
    @DisplayName("패스워드 정책 등록 성공: TSID 사전 발급 및 히스토리가 함께 적재된다")
    void createPasswordPolicy_Success() {
        // given
        PasswordPolicyCreateRequestDto request = PasswordPolicyCreateRequestDto.builder()
                .factoryName("INSERT")
                .policyName("DEFAULT_POLICY")
                .isActive("Y")
                .eventName("PasswordPolicyCreated")
                .eventUser("admin")
                .eventComment("신규 정책 생성")
                .build();

        PasswordPolicyHistoryEntity historyEntity = new PasswordPolicyHistoryEntity();

        when(passwordPolicyRepository.findByFactoryNameAndPolicyName("INSERT", "DEFAULT_POLICY")).thenReturn(Optional.empty());
        when(passwordPolicyRepository.save(any(PasswordPolicy.class))).thenAnswer(new Answer<PasswordPolicy>() {
            @Override
            public PasswordPolicy answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(passwordPolicyMapper.toHistoryEntity(any(PasswordPolicy.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(PasswordPolicyHistoryEntity.class))).thenReturn(historyEntity);

        // when
        PasswordPolicyResponse response = passwordPolicyService.createPasswordPolicy(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("INSERT", response.getFactoryName());
        assertEquals("DEFAULT_POLICY", response.getPolicyName());
        assertEquals("Y", response.getIsActive());

        verify(passwordPolicyRepository).save(any(PasswordPolicy.class));
        verify(historyService).saveHistory(any(PasswordPolicyHistoryEntity.class));
    }

    @Test
    @DisplayName("패스워드 정책 등록 실패: 동일 공장에 동일 정책명이 이미 존재할 경우 예외 발생")
    void createPasswordPolicy_Duplicate_ThrowsException() {
        // given
        final PasswordPolicyCreateRequestDto request = PasswordPolicyCreateRequestDto.builder()
                .factoryName("INSERT")
                .policyName("DEFAULT_POLICY")
                .build();

        when(passwordPolicyRepository.findByFactoryNameAndPolicyName("INSERT", "DEFAULT_POLICY")).thenReturn(Optional.of(samplePolicy));

        // when & then
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                passwordPolicyService.createPasswordPolicy(request);
            }
        });
        verify(passwordPolicyRepository, never()).save(any(PasswordPolicy.class));
        verify(historyService, never()).saveHistory(any());
    }

    @Test
    @DisplayName("패스워드 정책 수정 성공: 정보 변경 후 히스토리가 적재된다")
    void updatePasswordPolicy_Success() {
        // given
        Long targetId = samplePolicy.getId();
        PasswordPolicyUpdateRequestDto updateDto = PasswordPolicyUpdateRequestDto.builder()
                .policyName("SECURITY_POLICY_V2")
                .isActive("N")
                .eventName("PasswordPolicyModified")
                .eventUser("operator")
                .eventComment("정책 수정")
                .build();

        PasswordPolicyHistoryEntity historyEntity = new PasswordPolicyHistoryEntity();

        when(passwordPolicyRepository.findById(targetId)).thenReturn(Optional.of(samplePolicy));
        when(passwordPolicyRepository.findByFactoryNameAndPolicyName("INSERT", "SECURITY_POLICY_V2")).thenReturn(Optional.empty());
        when(passwordPolicyRepository.save(any(PasswordPolicy.class))).thenAnswer(new Answer<PasswordPolicy>() {
            @Override
            public PasswordPolicy answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(passwordPolicyMapper.toHistoryEntity(any(PasswordPolicy.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(PasswordPolicyHistoryEntity.class))).thenReturn(historyEntity);

        // when
        PasswordPolicyResponse response = passwordPolicyService.updatePasswordPolicy(targetId, updateDto);

        // then
        assertNotNull(response);
        assertEquals("SECURITY_POLICY_V2", response.getPolicyName());
        assertEquals("N", response.getIsActive());
        verify(passwordPolicyRepository).save(any(PasswordPolicy.class));
        verify(historyService).saveHistory(any(PasswordPolicyHistoryEntity.class));
    }

    @Test
    @DisplayName("패스워드 정책 단건 삭제 성공: 삭제 히스토리가 기록된 후 deleteById가 호출된다")
    void deletePasswordPolicy_Success() {
        // given
        Long targetId = samplePolicy.getId();
        PasswordPolicyHistoryEntity historyEntity = new PasswordPolicyHistoryEntity();

        when(passwordPolicyRepository.findById(targetId)).thenReturn(Optional.of(samplePolicy));
        when(passwordPolicyMapper.toHistoryEntity(any(PasswordPolicy.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(PasswordPolicyHistoryEntity.class))).thenReturn(historyEntity);

        // when
        passwordPolicyService.deletePasswordPolicy(targetId, "admin", "정책 삭제");

        // then
        verify(historyService).saveHistory(any(PasswordPolicyHistoryEntity.class));
        verify(passwordPolicyRepository).deleteById(targetId);
    }

    @Test
    @DisplayName("패스워드 정책 복수 벌크 삭제 성공: 각 대상별 삭제 이력이 남고 deleteAllByIdInBatch가 실행된다")
    void deletePasswordPolicies_BatchSuccess() {
        // given
        List<Long> ids = new ArrayList<>();
        ids.add(101L);
        ids.add(102L);

        PasswordPolicy p1 = PasswordPolicy.builder().id(101L).policyName("정책1").build();
        PasswordPolicy p2 = PasswordPolicy.builder().id(102L).policyName("정책2").build();
        PasswordPolicyHistoryEntity historyEntity = new PasswordPolicyHistoryEntity();

        when(passwordPolicyRepository.findById(101L)).thenReturn(Optional.of(p1));
        when(passwordPolicyRepository.findById(102L)).thenReturn(Optional.of(p2));
        when(passwordPolicyMapper.toHistoryEntity(any(PasswordPolicy.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(PasswordPolicyHistoryEntity.class))).thenReturn(historyEntity);

        // when
        passwordPolicyService.deletePasswordPolicies(ids, "ADMIN", "다건 정책 삭제");

        // then
        verify(historyService, times(2)).saveHistory(any(PasswordPolicyHistoryEntity.class));
        verify(passwordPolicyRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    @DisplayName("패스워드 정책 조건 목록 조회 성공: Page 객체로 매핑되어 반환된다")
    void findPasswordPolicies_Success() {
        // given
        PasswordPolicySearchCondition condition = new PasswordPolicySearchCondition();
        condition.setFactoryName("INSERT");

        Pageable pageable = PageRequest.of(0, 20);
        List<PasswordPolicy> policyList = new ArrayList<>();
        policyList.add(samplePolicy);

        when(passwordPolicyRepository.findAll()).thenReturn(policyList);

        // when
        Page<PasswordPolicyResponse> result = passwordPolicyService.findPasswordPolicies(condition, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("DEFAULT_POLICY", result.getContent().get(0).getPolicyName());
    }
}
