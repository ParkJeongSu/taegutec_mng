package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "패스워드 정책 등록 요청 DTO")
public class PasswordPolicyCreateRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "정책명", example = "DEFAULT_POLICY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyName;

    @Schema(description = "사용 여부 (Y/N, 미입력 시 'Y')", example = "Y", defaultValue = "Y")
    private String isActive;

    @Schema(description = "이벤트 이름", example = "PasswordPolicyCreated")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial password policy creation")
    private String eventComment;
}
