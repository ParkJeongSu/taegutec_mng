package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.UserGroupMenuAuthCreateCommand;
import kr.co.aim.domain.command.UserGroupMenuAuthUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupMenuAuth implements HasTransactionInfo {

    private Long id;
    private String factoryName;
    private Long userGroupId;
    private Long menuId;
    private String authSelect;
    private String authSave;
    private String authDelete;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static UserGroupMenuAuth create(UserGroupMenuAuthCreateCommand command) {
        return UserGroupMenuAuth.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .userGroupId(command.getUserGroupId())
                .menuId(command.getMenuId())
                .authSelect(command.getAuthSelect() != null ? command.getAuthSelect() : "N")
                .authSave(command.getAuthSave() != null ? command.getAuthSave() : "N")
                .authDelete(command.getAuthDelete() != null ? command.getAuthDelete() : "N")
                .eventName(command.getTransactionInfo() != null ? command.getTransactionInfo().eventName() : "UserGroupMenuAuthCreated")
                .eventTime(command.getTransactionInfo() != null ? command.getTransactionInfo().eventTime() : LocalDateTime.now())
                .eventUser(command.getTransactionInfo() != null ? command.getTransactionInfo().eventUser() : "SYSTEM")
                .eventComment(command.getTransactionInfo() != null ? command.getTransactionInfo().eventComment() : "User group menu auth created")
                .build();
    }

    public UserGroupMenuAuth update(UserGroupMenuAuthUpdateCommand command) {
        if (command.getAuthSelect() != null) {
            setAuthSelect(command.getAuthSelect());
        }
        if (command.getAuthSave() != null) {
            setAuthSave(command.getAuthSave());
        }
        if (command.getAuthDelete() != null) {
            setAuthDelete(command.getAuthDelete());
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
