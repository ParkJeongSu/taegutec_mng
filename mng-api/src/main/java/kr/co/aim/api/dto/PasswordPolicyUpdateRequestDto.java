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
@Schema(description = "패스워드 정책 수정 요청 DTO")
public class PasswordPolicyUpdateRequestDto {

    @Schema(description = "정책명", example = "ENHANCED_SECURITY_POLICY")
    private String policyName;

    @Schema(description = "사용 여부 (Y/N)", example = "Y")
    private String isActive;

    @Schema(description = "이벤트 이름", example = "PasswordPolicyModified")
    private String eventName;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Password policy updated")
    private String eventComment;
}
