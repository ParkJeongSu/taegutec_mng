package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.UserGroupMemberCreateCommand;
import kr.co.aim.domain.command.UserGroupMemberUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupMember implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private Long userId;
    private String employeeId;
    private String userName;
    private Long userGroupId;
    private String userGroupName;
    private String groupDescription;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static UserGroupMember create(UserGroupMemberCreateCommand command) {
        return UserGroupMember.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .userId(command.getUserId())
                .userGroupId(command.getUserGroupId())
                .eventName(command.getTransactionInfo() != null ? command.getTransactionInfo().eventName() : "UserGroupMemberCreated")
                .eventTime(command.getTransactionInfo() != null ? command.getTransactionInfo().eventTime() : LocalDateTime.now())
                .eventUser(command.getTransactionInfo() != null ? command.getTransactionInfo().eventUser() : "SYSTEM")
                .eventComment(command.getTransactionInfo() != null ? command.getTransactionInfo().eventComment() : "User group member created")
                .build();
    }

    public UserGroupMember update(UserGroupMemberUpdateCommand command) {
        if (command.getFactoryName() != null) {
            setFactoryName(command.getFactoryName());
        }
        if (command.getUserId() != null) {
            setUserId(command.getUserId());
        }
        if (command.getUserGroupId() != null) {
            setUserGroupId(command.getUserGroupId());
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
