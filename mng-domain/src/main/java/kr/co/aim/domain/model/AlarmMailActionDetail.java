package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AlarmMailActionDetailCreateCommand;
import kr.co.aim.domain.command.AlarmMailActionDetailUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class AlarmMailActionDetail implements HasTransactionInfo {
    private Long id;
    private Long alarmActionId;
    private Long alarmActionUserGroupId;
    private String subject;
    private String contents;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static AlarmMailActionDetail create(AlarmMailActionDetailCreateCommand command){
        return AlarmMailActionDetail.builder()
                .alarmActionId(command.getAlarmActionId())
                .alarmActionUserGroupId(command.getAlarmActionUserGroupId())
                .subject(command.getSubject())
                .contents(command.getContents())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
    public void changeAlarmMailActionDetail(AlarmMailActionDetailUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setSubject(command.getSubject());
        this.setContents(command.getContents());
    }

}
