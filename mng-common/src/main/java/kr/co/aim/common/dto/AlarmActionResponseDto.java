package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AlarmActionResponseDto {

    private Long id;
    private String alarmActionName;
    private String actionType;
    private Long alarmDefId;
    private String alarmDefName;
    private String description;
    private String dataState;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;


    @QueryProjection
    public AlarmActionResponseDto(
            Long id,
            String alarmActionName,
            String actionType,
            Long alarmDefId,
            String alarmDefName,
            String description,
            String dataState,
            String checkOutState,
            LocalDateTime checkOutTime,
            String checkOutUser,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    )
    {
        this.id = id ;
        this.alarmActionName = alarmActionName ;
        this.actionType = actionType ;
        this.alarmDefId = alarmDefId ;
        this.alarmDefName = alarmDefName ;
        this.description = description ;
        this.dataState = dataState ;
        this.checkOutState = checkOutState ;
        this.checkOutTime = checkOutTime ;
        this.checkOutUser = checkOutUser ;
        this.eventName = eventName ;
        
        this.eventTime = eventTime ;
        this.eventUser = eventUser ;
        this.eventComment = eventComment ;
    }
}