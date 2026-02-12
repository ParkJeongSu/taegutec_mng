package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.SystemDefCreateCommand;
import kr.co.aim.domain.command.SystemDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SystemDef implements HasTransactionInfo {
    private Long id;
    private String systemDefName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static SystemDef create(SystemDefCreateCommand command){
        return SystemDef.builder()
                .systemDefName(command.getSystemDefName())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeSystemDef(SystemDefUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }
}
