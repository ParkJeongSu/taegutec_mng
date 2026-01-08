package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AuthMenuCreateCommand;
import kr.co.aim.domain.command.AuthMenuUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthorityMenus implements HasTransactionInfo {

    private Long id;

    private String authorityName;

    private String description;

    private String checkOutState;

    private LocalDateTime checkOutTime;

    private String checkOutUser;

    private String dataState;

    private String eventName;

    private LocalDateTime eventTime;

    private String eventUser;

    private String eventComment;

    public static AuthorityMenus create(AuthMenuCreateCommand command){
        return AuthorityMenus.builder()
                .authorityName("")
                .description("")
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void ChangeAuthorityMenus(AuthMenuUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }
}
