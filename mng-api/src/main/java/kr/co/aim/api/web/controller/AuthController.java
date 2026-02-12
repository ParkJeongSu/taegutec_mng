package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.AuthorityService;
import kr.co.aim.api.service.UserService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authority", description = "권한 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthorityService authorityService;
    private final UserService userService;

    @Operation(summary = "권한 생성", description = "권한을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/auth
    @PostMapping
    public ResponseEntity<AuthorityResponseDto> createAuthority(@RequestBody AuthorityCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Authority authority = authorityService.createAuthority(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        AuthorityResponseDto responseDto =
                AuthorityResponseDto.builder()
                        .id(authority.getId())
                        .authorityName(authority.getAuthorityName())
                        .eventName(authority.getEventName())
                        .eventTime(authority.getEventTime())
                        .eventUser(authority.getEventUser())
                        .eventComment(authority.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 Authority 정보 변경", description = "사용자 ID를 이용하여 특정 사용자의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/auth/{authority-id}
    @PatchMapping("/{authority-id}")
    public ResponseEntity<AuthorityResponseDto> changeAuthority(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("authority-id") Long authorityId,
            @RequestBody AuthorityUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Authority authority = authorityService.chagneAuthority(authorityId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        AuthorityResponseDto responseDto = AuthorityResponseDto.builder()
                .id(authority.getId())
                .authorityName(authority.getAuthorityName())
                .description(authority.getDescription())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/auth/
    @GetMapping
    public ResponseEntity<Page<AuthorityResponseDto>> getAuthority(
            AuthoritySearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<AuthorityResponseDto> userPage = authorityService.findAuthority(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAuthority(@RequestBody DeleteItemListDto request) {
        authorityService.deleteUsersByIds(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

    // TODO: 아직 회원가입 화면은 안 만들었음 추후 만들기
    @PostMapping("/join")
    public ResponseEntity<Long> join(@RequestBody MemberJoinRequestDto memberJoinRequestDto) {
        Long savedId = userService.join(memberJoinRequestDto);
        return new ResponseEntity<>(savedId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        TokenDto tokenDto = userService.login(memberLoginRequestDto);
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        // 현재 인증된 사용자의 정보를 가져올 수 있습니다.
        // 여기서는 간단히 성공 메시지만 반환합니다.
        return new ResponseEntity<>("인증 성공!", HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenDto tokenDto = userService.reissue(tokenRequestDto);
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

}
