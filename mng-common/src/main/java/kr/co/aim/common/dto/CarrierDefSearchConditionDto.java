package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CarrierDefSearchConditionDto {
    private Long id;
    private String carrierDefName;
    private String factoryName;
    private String description;
    private String carrierType;
    private String carrierDetailType;
    private Integer defaultCapacity;
    private Integer useCountLimit;
    private Integer useDurationLimit;
    private Integer countLimitPerClean;
    private Integer durationLimitPerClean;
    private Integer cleanCountLimit;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
