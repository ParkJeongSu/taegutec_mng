package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmActionCreateRequestDto {

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