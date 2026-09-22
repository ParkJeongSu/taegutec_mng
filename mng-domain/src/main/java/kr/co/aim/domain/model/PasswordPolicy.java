package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordPolicy implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private String policyName;
    private String isActive;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
