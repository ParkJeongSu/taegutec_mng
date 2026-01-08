package kr.co.aim.domain.model;

import jakarta.persistence.*;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.common.handler.NotificationHandler;
import kr.co.aim.domain.command.AlarmActionCreateCommand;
import kr.co.aim.domain.command.AlarmActionUpdateCommand;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
@Slf4j
public class AlarmAction implements HasTransactionInfo {
    private Long id;
    private String alarmActionName;
    private String actionType;
    private Long alarmDefId;
    private String description;
    private String dataState;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;


    public static AlarmAction create(AlarmActionCreateCommand command){
        return AlarmAction.builder()
                .alarmActionName(command.getAlarmActionName())
                .actionType(command.getActionType())
                .alarmDefId(command.getAlarmDefId())
                .description(command.getDescription())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public void changeAlarmAction(AlarmActionUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setActionType(command.getActionType());
        this.setAlarmDefId(command.getAlarmDefId());
        this.setDescription(command.getDescription());

    }

    
    /*
    // TODO : 이건 서비스 Layer 로 올리자 아니다. 그냥 Detail 하고 유저List를 주입받아서 보내는 형태로 가자..
    public void mailexecute(Map<String, NotificationHandler> notificationServices) {
        NotificationHandler t = notificationServices.get("MAIL");
        log.info("mail test");

        for(AlarmMailActionDetailEntity detail : alarmMailActionDetailEntities){
            AlarmActionUserGroupEntity group = detail.getAlarmActionUserGroup();
            log.info(group.toString());
            List<AlarmActionUserGroupUsersEntity> userList = group.getAlarmActionUserGroupUsers();
            for(AlarmActionUserGroupUsersEntity user : userList){
                log.info(user.toString());
                //t.send(user.getUserId().toString(),"System",detail.getSubject(),detail.getContents());
            }
        }
    }
    */
    
}
