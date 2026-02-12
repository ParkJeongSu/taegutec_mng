package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.AlarmState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AlarmCreateCommand;
import kr.co.aim.domain.command.AlarmReportCommand;
import kr.co.aim.domain.command.AlarmUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class Alarm implements HasTransactionInfo {
    private Long id;
    private Long alarmDefId;
    private String equipmentName;
    private String alarmState;
    private LocalDateTime createTime;
    private LocalDateTime clearTime;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Alarm create(AlarmCreateCommand command){
        return Alarm.builder()
                .id(TsidUtils.nextId())
                .alarmDefId(command.getAlarmDefId())
                .equipmentName(command.getEquipmentName())
                .alarmState(command.getAlarmState().name())
                .createTime(command.getTransactionInfo().eventTime())
                //.clearTime()
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void alarmReport(AlarmReportCommand command){
        this.apply(command.getTransactionInfo());
        this.setAlarmState(command.getAlarmState().name());
        if(command.getAlarmState().name().equals(AlarmState.SET.getValue())){
            this.setCreateTime(command.getTransactionInfo().eventTime());
        }
        else{
            this.setClearTime(command.getTransactionInfo().eventTime());
        }
    }
    public void changeAlarm(AlarmUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setAlarmState(command.getAlarmState().name());
        if(command.getAlarmState().name().equals(AlarmState.SET.getValue())){
            this.setCreateTime(command.getTransactionInfo().eventTime());
        }
        else{
            this.setClearTime(command.getTransactionInfo().eventTime());
        }
    }
}
