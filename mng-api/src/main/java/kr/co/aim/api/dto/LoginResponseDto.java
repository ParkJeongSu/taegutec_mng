package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 ID", example = "admin")
    private String userId;

    @Schema(description = "사용자 이름", example = "관리자")
    private String userName;

    @Schema(description = "사용자 부서", example = "생산관리팀")
    private String departmentName;

    @Schema(description = "사용자 권한/그룹 목록", example = "[\"ADMIN\", \"OPERATOR\"]")
    private List<String> roles;
}
