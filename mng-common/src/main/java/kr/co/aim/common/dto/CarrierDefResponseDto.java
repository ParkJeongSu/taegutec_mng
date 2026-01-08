package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class CarrierDefResponseDto {
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
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public CarrierDefResponseDto(
            Long id,
            String carrierDefName,
            String description,
            String carrierType,
            String carrierDetailType,
            Integer defaultCapacity,
            Integer useCountLimit,
            Integer useDurationLimit,
            Integer countLimitPerClean,
            Integer durationLimitPerClean,
            Integer cleanCountLimit,
            String checkOutState,
            LocalDateTime checkOutTime,
            String checkOutUser,
            String dataState,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    )
    {
        this.id = id;
        this.carrierDefName = carrierDefName;
        this.description = description;
        this.carrierType = carrierType;
        this.carrierDetailType = carrierDetailType;
        this.defaultCapacity = defaultCapacity;
        this.useCountLimit = useCountLimit;
        this.useDurationLimit = useDurationLimit;
        this.countLimitPerClean = countLimitPerClean;
        this.durationLimitPerClean = durationLimitPerClean;
        this.cleanCountLimit = cleanCountLimit;
        this.checkOutState = checkOutState;
        this.checkOutTime = checkOutTime;
        this.checkOutUser = checkOutUser;
        this.dataState = dataState;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}