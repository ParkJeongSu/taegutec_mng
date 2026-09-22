package kr.co.aim.api.service;

import kr.co.aim.api.dto.SysUserCreateRequestDto;
import kr.co.aim.api.dto.SysUserResponse;
import kr.co.aim.api.dto.SysUserUpdateRequestDto;
import kr.co.aim.api.dto.UserGroupMemberResponse;
import kr.co.aim.common.condition.SysUserSearchCondition;
import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.SysUserHistoryEntity;
import kr.co.aim.infra.persistence.mapper.SysUserMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupMemberRepository userGroupMemberRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordPolicyService passwordPolicyService;

    @InjectMocks
    private SysUserService sysUserService;

    private SysUser sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = SysUser.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .userId("admin")
                .userName("관리자")
                .passwordHash("$2a$10$encodedPasswordHash")
                .departmentId(1L)
                .departmentName("생산관리팀")
                .email("admin@taegutec.co.kr")
                .phoneNumber("010-1234-5678")
                .userState("ACTIVE")
                .failedLoginCount(0)
                .eventName("UserCreated")
                .eventTime(LocalDateTime.now())
                .eventUser("SYSTEM")
                .eventComment("Initial Admin User")
                .build();
    }

    @Test
    @DisplayName("사용자 등록 성공: 비밀번호 정책 검증 통과 후 암호화되고 TSID와 히스토리가 함께 적재된다")
    void createUser_Success() {
        // given
        SysUserCreateRequestDto request = SysUserCreateRequestDto.builder()
                .factoryName("INSERT")
                .userId("newUser")
                .password("PlainPass123")
                .userName("신규사용자")
                .departmentId(1L)
                .email("new@taegutec.co.kr")
                .phoneNumber("010-9999-8888")
                .userState("ACTIVE")
                .eventName("UserCreated")
                .eventUser("admin")
                .eventComment("신규 등록")
                .build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "newUser")).thenReturn(Optional.empty());
        doNothing().when(passwordPolicyService).validatePasswordRules("INSERT", "PlainPass123");
        when(passwordEncoder.encode("PlainPass123")).thenReturn("$2a$10$encodedPassword123");
        when(userRepository.save(any(SysUser.class))).thenAnswer(new Answer<SysUser>() {
            @Override
            public SysUser answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(sampleUser));
        when(sysUserMapper.toHistoryEntity(any(SysUser.class))).thenReturn(mock(SysUserHistoryEntity.class));

        // when
        SysUserResponse response = sysUserService.createUser(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("INSERT", response.getFactoryName());
        assertEquals("admin", response.getUserId());
        assertEquals("생산관리팀", response.getDepartmentName());

        verify(passwordPolicyService).validatePasswordRules("INSERT", "PlainPass123");
        verify(passwordEncoder).encode("PlainPass123");
        verify(userRepository).save(any(SysUser.class));
        verify(historyService).saveHistory(any(SysUserHistoryEntity.class));
    }

    @Test
    @DisplayName("사용자 등록 실패: 동일 공장에 중복된 사용자 ID가 이미 존재할 경우 예외 발생")
    void createUser_DuplicateUserId_ThrowsException() {
        // given
        final SysUserCreateRequestDto request = SysUserCreateRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("PlainPass123")
                .userName("중복관리자")
                .build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "admin")).thenReturn(Optional.of(sampleUser));

        // when & then
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                sysUserService.createUser(request);
            }
        });
        verify(passwordPolicyService, never()).validatePasswordRules(anyString(), anyString());
        verify(userRepository, never()).save(any(SysUser.class));
        verify(historyService, never()).saveHistory(any());
    }

    @Test
    @DisplayName("사용자 수정 성공: 정보 변경 및 새 비밀번호 정책 검증 후 암호화되고 히스토리가 적재된다")
    void updateUser_Success_WithNewPassword() {
        // given
        Long targetId = sampleUser.getId();
        SysUserUpdateRequestDto updateDto = SysUserUpdateRequestDto.builder()
                .id(targetId)
                .factoryName("INSERT")
                .password("NewSecretPass1")
                .userName("수정된관리자")
                .email("modified@taegutec.co.kr")
                .eventName("UserModified")
                .eventUser("operator")
                .eventComment("비밀번호 및 이름 변경")
                .build();

        when(userRepository.findById(targetId)).thenReturn(Optional.of(sampleUser));
        doNothing().when(passwordPolicyService).validatePasswordRules("INSERT", "NewSecretPass1");
        when(passwordEncoder.encode("NewSecretPass1")).thenReturn("$2a$10$newSecretPassHash");
        when(userRepository.save(any(SysUser.class))).thenAnswer(new Answer<SysUser>() {
            @Override
            public SysUser answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(sysUserMapper.toHistoryEntity(any(SysUser.class))).thenReturn(mock(SysUserHistoryEntity.class));

        // when
        SysUserResponse response = sysUserService.updateUser(targetId, updateDto);

        // then
        assertNotNull(response);
        assertEquals("생산관리팀", response.getDepartmentName());
        verify(passwordPolicyService).validatePasswordRules("INSERT", "NewSecretPass1");
        verify(passwordEncoder).encode("NewSecretPass1");
        verify(userRepository).save(any(SysUser.class));
        verify(historyService).saveHistory(any(SysUserHistoryEntity.class));
    }

    @Test
    @DisplayName("사용자 단건 삭제 성공: 삭제 히스토리가 기록된 후 deleteById가 호출된다")
    void deleteUser_Success() {
        // given
        Long targetId = sampleUser.getId();
        when(userRepository.findById(targetId)).thenReturn(Optional.of(sampleUser));
        when(sysUserMapper.toHistoryEntity(any(SysUser.class))).thenReturn(mock(SysUserHistoryEntity.class));

        // when
        sysUserService.deleteUser(targetId, "admin", "사용자 삭제");

        // then
        verify(historyService).saveHistory(any(SysUserHistoryEntity.class));
        verify(userRepository).deleteById(targetId);
    }

    @Test
    @DisplayName("사용자 복수 벌크 삭제 성공: 각 대상별 삭제 이력이 남고 deleteAllByIdInBatch가 실행된다")
    void deleteUsers_BatchSuccess() {
        // given
        List<Long> ids = new ArrayList<>();
        ids.add(101L);
        ids.add(102L);

        SysUser u1 = SysUser.builder().id(101L).userId("u1").build();
        SysUser u2 = SysUser.builder().id(102L).userId("u2").build();

        when(userRepository.findById(101L)).thenReturn(Optional.of(u1));
        when(userRepository.findById(102L)).thenReturn(Optional.of(u2));
        when(sysUserMapper.toHistoryEntity(any(SysUser.class))).thenReturn(mock(SysUserHistoryEntity.class));

        // when
        sysUserService.deleteUsers(ids, "ADMIN", "다건 삭제");

        // then
        verify(historyService, times(2)).saveHistory(any(SysUserHistoryEntity.class));
        verify(userRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    @DisplayName("사용자 조건 목록 조회 성공: 부서명이 포함된 Page 객체로 매핑되어 반환된다")
    void findUsers_Success() {
        // given
        SysUserSearchCondition condition = new SysUserSearchCondition();
        condition.setFactoryName("INSERT");

        Pageable pageable = PageRequest.of(0, 20);
        List<SysUser> userList = new ArrayList<>();
        userList.add(sampleUser);
        Page<SysUser> page = new PageImpl<>(userList, pageable, 1);

        when(userRepository.findUserWithConditions(condition, pageable)).thenReturn(page);

        // when
        Page<SysUserResponse> result = sysUserService.findUsers(condition, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("admin", result.getContent().get(0).getUserId());
        assertEquals("생산관리팀", result.getContent().get(0).getDepartmentName());
    }

    @Test
    @DisplayName("사용자-그룹 매핑 목록 조회 성공: 3-Way JOIN 결과가 Page 객체로 반환된다")
    void findUserGroupMembers_Success() {
        // given
        UserGroupMember member = UserGroupMember.builder()
                .id(999L)
                .factoryName("INSERT")
                .userId(877810665130787535L)
                .employeeId("admin")
                .userName("관리자")
                .userGroupId(10L)
                .userGroupName("ADMIN")
                .groupDescription("관리자 그룹")
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        List<UserGroupMember> memberList = new ArrayList<>();
        memberList.add(member);
        Page<UserGroupMember> page = new PageImpl<>(memberList, pageable, 1);

        when(userGroupMemberRepository.findUserGroupMembersWithDetails(eq(877810665130787535L), isNull(), isNull(), eq(pageable))).thenReturn(page);

        // when
        Page<UserGroupMemberResponse> result = sysUserService.findUserGroupMembers(877810665130787535L, null, null, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("admin", result.getContent().get(0).getEmployeeId());
        assertEquals("ADMIN", result.getContent().get(0).getUserGroupName());
    }
}
