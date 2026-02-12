package kr.co.aim.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AlarmDefResponseDto {

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

    @QueryProjection
    public AlarmDefResponseDto(
            Long id,
            String alarmDefName,
            String alarmType,
            String description,
            String alarmLevel,
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
         this.id = id;
         this.alarmDefName = alarmDefName;
         this.alarmType = alarmType;
         this.description = description;
         this.alarmLevel = alarmLevel;
         this.dataState = dataState;
         this.checkOutState = checkOutState;
         this.checkOutTime = checkOutTime;
         this.checkOutUser = checkOutUser;
         this.eventName = eventName;
         
         this.eventTime = eventTime;
         this.eventUser = eventUser;
         this.eventComment = eventComment;
    }
}