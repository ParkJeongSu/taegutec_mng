package kr.co.aim.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmActionUserGroupUsersCreateRequestDto {

    private Long id;
    private Long alarmActionUserGroupId;
    private String userId;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}