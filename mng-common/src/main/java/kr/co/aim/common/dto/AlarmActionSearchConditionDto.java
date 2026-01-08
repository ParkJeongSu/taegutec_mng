package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmActionSearchConditionDto {

    private Long id;
    private String alarmActionName;
    private String actionType;
    private Long alarmDefId;
}