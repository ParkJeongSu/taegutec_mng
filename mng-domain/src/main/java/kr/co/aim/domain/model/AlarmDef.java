package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AlarmCreateCommand;
import kr.co.aim.domain.command.AlarmDefCreateCommand;
import kr.co.aim.domain.command.AlarmDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class AlarmDef implements HasTransactionInfo {
    private Long id;
    private String alarmDefName;
    private String alarmType;
    private String description;
    private String alarmLevel;
    private String dataState;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static AlarmDef create(AlarmDefCreateCommand command){
        return AlarmDef.builder()
                .alarmDefName(command.getAlarmDefName())
                .alarmType(command.getAlarmType())
                .description(command.getDescription())
                .alarmLevel(command.getAlarmLevel())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeAlarmDef(AlarmDefUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDescription(command.getDescription());
        this.setAlarmLevel(command.getAlarmLevel());
        this.setAlarmType(command.getAlarmType());

    }
}
