package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AlarmResponseDto {

    private Long id;
    private Long alarmDefId;
    private String alarmDefName;
    private String equipmentName;
    private String alarmState;
    private LocalDateTime createTime;
    private LocalDateTime clearTime;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public AlarmResponseDto(
            Long id,
            Long alarmDefId,
            String alarmDefName,
            String equipmentName,
            String alarmState,
            LocalDateTime createTime,
            LocalDateTime clearTime,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id= id;
        this.alarmDefId= alarmDefId;
        this.alarmDefName= alarmDefName;
        this.equipmentName= equipmentName;
        this.alarmState= alarmState;
        this.createTime= createTime;
        this.clearTime= clearTime;
        this.eventName= eventName;
        
        this.eventTime= eventTime;
        this.eventUser= eventUser;
        this.eventComment= eventComment;    }
}