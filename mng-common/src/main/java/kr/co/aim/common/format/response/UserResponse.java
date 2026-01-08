package kr.co.aim.common.format.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long id;
    @Schema(description = "사용자 이름", example = "홍길동")
    private final String username;
    @Schema(description = "사용자 이메일", example = "gildong@example.com")
    private final String email;

    public UserResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
