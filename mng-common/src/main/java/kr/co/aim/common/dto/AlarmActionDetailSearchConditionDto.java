package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class AlarmActionDetailSearchConditionDto {

    private Long alarmActionId;
    private Long alarmActionUserGroupId;
    private String subject;
    private String contents;

}