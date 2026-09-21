package kr.co.aim.api.service;

import kr.co.aim.api.jwt.JwtTokenProvider;
import kr.co.aim.api.dto.LoginRequestDto;
import kr.co.aim.api.dto.LoginResponseDto;
import kr.co.aim.domain.model.Department;
import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.model.UserGroup;
import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.DepartmentRepository;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.domain.repository.UserGroupRepository;
import kr.co.aim.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGroupMemberRepository userGroupMemberRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = SysUser.builder()
                .id(100L)
                .factoryName("INSERT")
                .userId("admin")
                .userName("관리자")
                .passwordHash("{bcrypt}$2a$10$encodedPassword")
                .departmentId(1L)
                .failedLoginCount(0)
                .build();
    }

    @Test
    @DisplayName("로그인 성공: 정상적인 계정 및 비밀번호인 경우 토큰과 사용자 정보를 반환한다")
    void loginSuccess() {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("password123")
                .build();

        List<UserGroupMember> members = new ArrayList<>();
        members.add(UserGroupMember.builder()
                .id(1L)
                .userId(100L)
                .userGroupId(10L)
                .build());
        members.add(UserGroupMember.builder()
                .id(2L)
                .userId(100L)
                .userGroupId(20L)
                .build());

        UserGroup g1 = UserGroup.builder().id(10L).userGroupName("ADMIN").build();
        UserGroup g2 = UserGroup.builder().id(20L).userGroupName("OPERATOR").build();
        Department dept = Department.builder().id(1L).departmentName("생산관리팀").build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(userGroupMemberRepository.findByUserId(100L)).thenReturn(members);
        when(userGroupRepository.findById(10L)).thenReturn(Optional.of(g1));
        when(userGroupRepository.findById(20L)).thenReturn(Optional.of(g2));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(jwtTokenProvider.createAccessToken(eq("admin"), anyList())).thenReturn("mocked-access-token");
        when(jwtTokenProvider.createRefreshToken("admin")).thenReturn("mocked-refresh-token");
        when(userRepository.save(any(SysUser.class))).thenReturn(testUser);

        // when
        LoginResponseDto response = authService.login(request);

        // then
        assertNotNull(response);
        assertEquals("mocked-access-token", response.getAccessToken());
        assertEquals("mocked-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("INSERT", response.getFactoryName());
        assertEquals("admin", response.getUserId());
        assertEquals("관리자", response.getUserName());
        assertEquals("생산관리팀", response.getDepartmentName());
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().contains("ADMIN"));
        assertTrue(response.getRoles().contains("OPERATOR"));

        verify(userRepository).save(argThat(user -> user.getFailedLoginCount() == 0 && "Login".equals(user.getEventName())));
    }

    @Test
    @DisplayName("로그인 실패: 사용자가 존재하지 않는 경우 IllegalArgumentException 발생")
    void loginFail_UserNotFound() {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .factoryName("INSERT")
                .userId("unknown")
                .password("password123")
                .build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "unknown")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호가 일치하지 않는 경우 실패 횟수 증가 및 예외 발생")
    void loginFail_PasswordMismatch() {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("wrongpassword")
                .build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(userRepository).save(argThat(user -> user.getFailedLoginCount() == 1 && "LoginFailed".equals(user.getEventName())));
    }

    @Test
    @DisplayName("그룹명 조회: UserGroupMember 목록으로 UserGroup 조회")
    void loginGroupFallback() {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("password123")
                .build();

        List<UserGroupMember> members = new ArrayList<>();
        members.add(UserGroupMember.builder()
                .id(1L)
                .userId(100L)
                .userGroupId(10L)
                .build());

        UserGroup userGroup = UserGroup.builder()
                .id(10L)
                .userGroupName("SUPER_ADMIN")
                .build();

        when(userRepository.findByFactoryNameAndUserId("INSERT", "admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(userGroupMemberRepository.findByUserId(100L)).thenReturn(members);
        when(userGroupRepository.findById(10L)).thenReturn(Optional.of(userGroup));
        when(jwtTokenProvider.createAccessToken(eq("admin"), anyList())).thenReturn("mocked-access-token");
        when(jwtTokenProvider.createRefreshToken("admin")).thenReturn("mocked-refresh-token");

        // when
        LoginResponseDto response = authService.login(request);

        // then
        assertNotNull(response);
        assertEquals(1, response.getRoles().size());
        assertEquals("SUPER_ADMIN", response.getRoles().get(0));
    }
}

