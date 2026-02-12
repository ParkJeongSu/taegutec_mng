package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmUpdateRequestDto {

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
}