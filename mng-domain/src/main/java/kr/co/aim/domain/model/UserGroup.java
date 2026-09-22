package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.UserGroupCreateCommand;
import kr.co.aim.domain.command.UserGroupUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private String userGroupName;
    private String description;
    private String useState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static UserGroup create(UserGroupCreateCommand command) {
        return UserGroup.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .userGroupName(command.getUserGroupName())
                .description(command.getDescription())
                .useState(command.getUseState() != null ? command.getUseState() : "ACTIVE")
                .eventName(command.getTransactionInfo() != null ? command.getTransactionInfo().eventName() : "UserGroupCreated")
                .eventTime(command.getTransactionInfo() != null ? command.getTransactionInfo().eventTime() : LocalDateTime.now())
                .eventUser(command.getTransactionInfo() != null ? command.getTransactionInfo().eventUser() : "SYSTEM")
                .eventComment(command.getTransactionInfo() != null ? command.getTransactionInfo().eventComment() : "UserGroup created")
                .build();
    }

    public UserGroup update(UserGroupUpdateCommand command) {
        if (command.getFactoryName() != null) {
            setFactoryName(command.getFactoryName());
        }
        if (command.getUserGroupName() != null) {
            setUserGroupName(command.getUserGroupName());
        }
        if (command.getDescription() != null) {
            setDescription(command.getDescription());
        }
        if (command.getUseState() != null) {
            setUseState(command.getUseState());
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
