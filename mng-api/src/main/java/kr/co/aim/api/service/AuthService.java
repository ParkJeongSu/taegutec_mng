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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AuthService {

    private final UserRepository userRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final UserGroupRepository userGroupRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(value = "mssqlTransactionManager")
    public LoginResponseDto login(LoginRequestDto request) {
        if (request == null || request.getFactoryName() == null || request.getUserId() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("공장 구분, 사용자 ID, 비밀번호를 모두 입력해주세요.");
        }

        String factoryName = request.getFactoryName().trim();
        String userId = request.getUserId().trim();
        String password = request.getPassword();

        // 1. 사용자 조회
        Optional<SysUser> userOptional = userRepository.findByFactoryNameAndUserId(factoryName, userId);
        if (!userOptional.isPresent()) {
            log.warn("Login failed: User not found [factoryName={}, userId={}]", factoryName, userId);
            throw new IllegalArgumentException("사용자 ID 또는 비밀번호가 일치하지 않습니다.");
        }

        SysUser user = userOptional.get();

        // 2. 비밀번호 검증
        boolean passwordMatched = false;
        if (user.getPasswordHash() != null) {
            try {
                passwordMatched = passwordEncoder.matches(password, user.getPasswordHash());
            } catch (Exception e) {
                log.warn("PasswordEncoder matching error, checking raw password match: {}", e.getMessage());
                if (user.getPasswordHash().equals(password)) {
                    passwordMatched = true;
                }
            }
            if (!passwordMatched && user.getPasswordHash().equals(password)) {
                passwordMatched = true;
            }
        }

        if (!passwordMatched) {
            log.warn("Login failed: Password mismatch [factoryName={}, userId={}]", factoryName, userId);
            int failedAttempts = (user.getFailedLoginCount() != null) ? user.getFailedLoginCount() + 1 : 1;
            user.setFailedLoginCount(failedAttempts);
            user.setEventName("LoginFailed");
            user.setEventTime(LocalDateTime.now());
            user.setEventUser(userId);
            user.setEventComment("Password mismatch");
            userRepository.save(user);

            throw new IllegalArgumentException("사용자 ID 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 사용자 소속 그룹(권한) 목록 조회
        List<String> roles = new ArrayList<>();
        if (user.getId() != null) {
            List<UserGroupMember> members = userGroupMemberRepository.findByUserId(user.getId());
            if (members != null) {
                for (UserGroupMember member : members) {
                    Optional<UserGroup> optionalUserGroup = userGroupRepository.findById(member.getUserGroupId());
                    String groupName = "";
                    if(optionalUserGroup.isPresent()) {
                        UserGroup userGroup = optionalUserGroup.get();
                        groupName = userGroup.getUserGroupName();
                    }
                    if (groupName == null || groupName.trim().isEmpty()) {
                        if (member.getUserGroupId() != null) {
                            Optional<UserGroup> groupOptional = userGroupRepository.findById(member.getUserGroupId());
                            if (groupOptional.isPresent()) {
                                groupName = groupOptional.get().getUserGroupName();
                            }
                        }
                    }
                    if (groupName != null && !groupName.trim().isEmpty()) {
                        groupName = groupName.trim();
                        boolean exists = false;
                        for (String r : roles) {
                            if (r.equalsIgnoreCase(groupName)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            roles.add(groupName);
                        }
                    }
                }
            }
        }

        // 4. Department 조회
        Optional<Department> optionalDepartment = departmentRepository.findById(user.getDepartmentId());
        String departmentName = "";
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            departmentName = department.getDepartmentName();
        }

        // 4. 로그인 성공 시 사용자 상태 갱신
        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginTime(now);
        user.setFailedLoginCount(0);
        user.setEventName("Login");
        user.setEventTime(now);
        user.setEventUser(userId);
        user.setEventComment("User logged in successfully");
        userRepository.save(user);

        // 5. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        log.info("Login successful: [factoryName={}, userId={}, roles={}]", factoryName, userId, roles);

        // 6. 응답 DTO 반환
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .factoryName(user.getFactoryName())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .departmentName(departmentName)
                .roles(roles)
                .build();
    }
}
