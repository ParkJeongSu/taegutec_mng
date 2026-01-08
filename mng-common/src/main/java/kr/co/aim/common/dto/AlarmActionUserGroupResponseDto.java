package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AlarmActionUserGroupResponseDto {

    private Long id;
    private String userGroupName;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public AlarmActionUserGroupResponseDto(
            Long id,
            String userGroupName,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment

    ){
        this.id = id;
        this.userGroupName = userGroupName;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}