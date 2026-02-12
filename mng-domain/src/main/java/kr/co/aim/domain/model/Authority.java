package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AuthorityCreateCommand;
import kr.co.aim.domain.command.AuthorityUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authority implements HasTransactionInfo {
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

    public static Authority create(AuthorityCreateCommand command){
        return Authority.builder()
                .authorityName(command.getAuthorityName())
                .description(command.getDescription())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();

    }

    public void changeAuthority(AuthorityUpdateCommand command){
        this.apply(command.getTransactionInfo());
        // TODO: 기존의 존재하는 authorityName 있으면 에러
        this.setAuthorityName(command.getAuthorityName());
        this.setDescription(command.getDescription());
    }
}
