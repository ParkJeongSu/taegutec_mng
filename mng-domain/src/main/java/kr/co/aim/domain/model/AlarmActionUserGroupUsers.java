package kr.co.aim.domain.model;

import jakarta.persistence.*;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AlarmActionUserGroupUsersCreateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class AlarmActionUserGroupUsers implements HasTransactionInfo {
    private Long id;
    private Long alarmActionUserGroupId;
    private String userId;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static AlarmActionUserGroupUsers create(AlarmActionUserGroupUsersCreateCommand command){
        return AlarmActionUserGroupUsers.builder()
                .alarmActionUserGroupId(command.getAlarmActionUserGroupId())
                .userId(command.getUserId())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

}
