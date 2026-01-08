package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AlarmActionDetailResponseDto {

    private Long id;
    private Long alarmActionId;
    private String alarmActionName;
    private Long alarmActionUserGroupId;
    private String userGroupName;
    private String subject;
    private String contents;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public AlarmActionDetailResponseDto(
            Long id,
            Long alarmActionId,
            String alarmActionName,
            Long alarmActionUserGroupId,
            String userGroupName,
            String subject,
            String contents,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    )
    {
        this.id = id;
        this.alarmActionId = alarmActionId;
        this.alarmActionName = alarmActionName;
        this.alarmActionUserGroupId = alarmActionUserGroupId;
        this.userGroupName = userGroupName;
        this.subject = subject;
        this.contents = contents;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}