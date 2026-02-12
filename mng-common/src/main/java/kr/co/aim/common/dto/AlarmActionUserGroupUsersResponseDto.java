package kr.co.aim.common.dto;

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
public class AlarmActionUserGroupUsersResponseDto {

    private Long id;
    private Long alarmActionUserGroupId;
    private String userGroupName;
    private String userId;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public AlarmActionUserGroupUsersResponseDto(
            Long id,
            Long alarmActionUserGroupId,
            String userGroupName,
            String userId,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id = id;
        this.alarmActionUserGroupId = alarmActionUserGroupId;
        this.userGroupName = userGroupName;
        this.userId = userId;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }

}