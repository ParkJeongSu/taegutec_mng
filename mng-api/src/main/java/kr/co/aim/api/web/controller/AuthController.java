package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.AuthService;
import kr.co.aim.api.dto.LoginRequestDto;
import kr.co.aim.api.dto.LoginResponseDto;
import kr.co.aim.common.annotation.ResponseAnnotation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 및 권한 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "공장 구분, 사용자 ID, 비밀번호를 검증하고 JWT 토큰과 사용자 권한 정보를 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
