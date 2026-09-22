package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.PasswordPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "패스워드 정책 응답 DTO")
public class PasswordPolicyResponse {

    @Schema(description = "정책 고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "정책명", example = "DEFAULT_POLICY")
    private String policyName;

    @Schema(description = "사용 여부 (Y/N)", example = "Y")
    private String isActive;

    @Schema(description = "이벤트 이름", example = "PasswordPolicyCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-22T14:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial password policy creation")
    private String eventComment;

    public static PasswordPolicyResponse fromDomain(PasswordPolicy domain) {
        if (domain == null) {
            return null;
        }
        return PasswordPolicyResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .policyName(domain.getPolicyName())
                .isActive(domain.getIsActive())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
