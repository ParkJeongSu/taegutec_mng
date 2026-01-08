package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class CarrierDefSearchConditionDto {
    private Long id;
    private String carrierDefName;
    private String description;
    private String carrierType;
    private String carrierDetailType;
    private Integer defaultCapacity;
    private Integer useCountLimit;
    private Integer useDurationLimit;
    private Integer countLimitPerClean;
    private Integer durationLimitPerClean;
    private Integer cleanCountLimit;
}