package kr.co.aim.common.condition;

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
@Schema(description = "패스워드 정책 검색 조건")
public class PasswordPolicySearchCondition {

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "정책명", example = "DEFAULT_POLICY")
    private String policyName;

    @Schema(description = "사용 여부 (Y/N)", example = "Y")
    private String isActive;
}
