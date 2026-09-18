package kr.co.aim.domain.model;

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
    private Integer minLength;
    private Integer maxLength;
    private String requireSpecialChar;
    private Integer expirationDays;
    private Integer maxFailedAttempts;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
