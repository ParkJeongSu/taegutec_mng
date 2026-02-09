package kr.co.aim.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TransportOrderResponseDto {

    private Long id;

    private String transportOrderName;

    private String description;

    private String transportType;

    private String transportOrderId;

    private String transportStatus;

    private Integer priority;

    private String galId;

    private String galWarehouse;

    private String fromWarehouse;

    private String fromZoneName;

    private String fromLocationId;

    private String toWarehouse;

    private String toZoneName;

    private String toLocationId;

    private String carrierName;

    private String carrierType;

    private String drivingProfile;

    private LocalDateTime createTime;

    private LocalDateTime releaseTime;

    private LocalDateTime completeTime;

    private String createUser;

    private String releaseUser;

    private String completeUser;
    
}