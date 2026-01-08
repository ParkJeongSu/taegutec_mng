package kr.co.aim.domain.model;

import jakarta.persistence.*;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AlarmActionUserGroupCreateCommand;
import kr.co.aim.domain.command.AlarmActionUserGroupUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class AlarmActionUserGroup implements HasTransactionInfo {
    private Long id;
    private String userGroupName;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static AlarmActionUserGroup create(AlarmActionUserGroupCreateCommand command){
        return AlarmActionUserGroup.builder()
                .userGroupName(command.getUserGroupName())
                .eventComment(command.getTransactionInfo().eventComment())
                .eventName(command.getTransactionInfo().eventName())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventTime(command.getTransactionInfo().eventTime())
                .build();
    }

    public void changeAlarmActionUserGoup(AlarmActionUserGroupUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }

}
