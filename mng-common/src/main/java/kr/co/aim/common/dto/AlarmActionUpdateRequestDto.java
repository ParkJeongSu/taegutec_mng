package kr.co.aim.common.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmActionUpdateRequestDto {

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
}