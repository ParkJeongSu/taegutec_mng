package kr.co.aim.api.dto;


import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
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

    public static TransportOrderResponseDto from (TransportOrderEntity entity){
        return TransportOrderResponseDto.builder()
                .id(entity.getId())
                .transportOrderName(entity.getTransportOrderName())
                .description(entity.getDescription())
                .transportType(entity.getTransportType())
                .transportOrderId(entity.getTransportOrderId())
                .transportStatus(entity.getTransportStatus())
                .priority(entity.getPriority())
                .galId(entity.getGalId())
                .galWarehouse(entity.getGalWarehouse())
//                .fromWarehouse(entity.getFromWarehouse())
//                .fromZoneName(entity.getFromZoneName())
//                .fromLocationId(entity.getFromLocationId())
//                .toWarehouse(entity.getToWarehouse())
//                .toZoneName(entity.getToZoneName())
//                .toLocationId(entity.getToLocationId())
                .carrierName(entity.getCarrierName())
                .carrierType(entity.getCarrierType())
                .drivingProfile(entity.getDrivingProfile())
                .createTime(entity.getCreateTime())
                .releaseTime(entity.getReleaseTime())
                .completeTime(entity.getCompleteTime())
                .createUser(entity.getCreateUser())
                .releaseUser(entity.getReleaseUser())
                .completeUser(entity.getCompleteUser())
                .build();
    }
    
}