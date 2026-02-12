package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.ExcelService;
import kr.co.aim.api.service.UserService;
import kr.co.aim.common.error.ExcelValidationException;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // 👈 Domain 계층의 Service에 의존
    private final ExcelService excelService; // 1. ExcelService 주입

    @Operation(summary = "특정 사용자 정보 변경", description = "사용자 ID를 이용하여 특정 사용자의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/users/{user-id}
    @PatchMapping("/{user-id}")
    public ResponseEntity<UserResponseDto> changeUser(
            @Parameter(description = "변경할 사용자의 ID", required = true, example = "1")
            @PathVariable("user-id") Long userId,
            @RequestBody UserUpdateRequestDto requestDto
    ) {
        // 3. 서비스 계층에 작업 위임
        User updatedUser = userService.changeUser(userId,requestDto);

        // 4. 결과 변환 및 HTTP 응답
        UserResponseDto responseDto =
                UserResponseDto.builder()
                        .id(updatedUser.getId())
                        .authorityId(updatedUser.getAuthorityId())
                        .userId(updatedUser.getUserId())
                        .email(updatedUser.getEmail())
                        .userName(updatedUser.getUserName())
                        .phone1(updatedUser.getPhone1())
                        .phone2(updatedUser.getPhone2())
                        .build();
        
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 사용자 정보 생성", description = "사용자 ID를 이용하여 특정 사용자의 정보를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/users
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        User updatedUser = userService.createUser(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        UserResponseDto responseDto =
                UserResponseDto.builder()
                        .id(updatedUser.getId())
                        .authorityId(updatedUser.getAuthorityId())
                        .userId(updatedUser.getUserId())
                        .email(updatedUser.getEmail())
                        .userName(updatedUser.getUserName())
                        .phone1(updatedUser.getPhone1())
                        .phone2(updatedUser.getPhone2())
                        .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "사용자 정보 삭제", description = "사용자의 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteUsers(@RequestBody DeleteItemListDto request) {
        userService.deleteUsersByIds(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 사용자 정보 생성", description = "사용자 ID를 이용하여 특정 사용자의 정보를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/users
    @PostMapping("/import")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            // 서비스 로직에서 (2)~(5) 검증 수행
            List<UserCreateRequestDto> importList = excelService.importData(file,UserCreateRequestDto.class);

            // importList 를 가지고
            userService.createUsers(importList);

            // 200 OK (성공)
            return ResponseEntity.ok().build();

        } catch (ExcelValidationException ex) {
            // (2)~(5) 검증 실패 시
            // ex.getErrors()는 ["3행: ...", "5행: ..."] 같은 List<String>

            // 400 Bad Request와 오류 메시지 목록 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Validation failed");
            errorResponse.put("errors", ex.getErrorMessages());

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            // 그 외 서버 내부 오류
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "An internal server error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



}
