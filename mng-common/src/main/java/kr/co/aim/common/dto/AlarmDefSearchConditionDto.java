package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmDefSearchConditionDto {

    private Long id;
    private String alarmDefName;
    private String alarmType;
    private String alarmLevel;
}