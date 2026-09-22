package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.PasswordPolicyCreateCommand;
import kr.co.aim.domain.command.PasswordPolicyUpdateCommand;
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

    public static PasswordPolicy create(PasswordPolicyCreateCommand command) {
        return PasswordPolicy.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .policyName(command.getPolicyName())
                .isActive(command.getIsActive() != null ? command.getIsActive() : "Y")
                .eventName(command.getTransactionInfo() != null ? command.getTransactionInfo().eventName() : "PasswordPolicyCreated")
                .eventTime(command.getTransactionInfo() != null ? command.getTransactionInfo().eventTime() : LocalDateTime.now())
                .eventUser(command.getTransactionInfo() != null ? command.getTransactionInfo().eventUser() : "SYSTEM")
                .eventComment(command.getTransactionInfo() != null ? command.getTransactionInfo().eventComment() : "Password policy created")
                .build();
    }

    public PasswordPolicy update(PasswordPolicyUpdateCommand command) {
        if (command.getPolicyName() != null) {
            setPolicyName(command.getPolicyName());
        }
        if (command.getIsActive() != null) {
            setIsActive(command.getIsActive());
        }
        if (command.getTransactionInfo() != null) {
            setEventName(command.getTransactionInfo().eventName());
            setEventTime(command.getTransactionInfo().eventTime());
            setEventUser(command.getTransactionInfo().eventUser());
            setEventComment(command.getTransactionInfo().eventComment());
        }
        return this;
    }
}
