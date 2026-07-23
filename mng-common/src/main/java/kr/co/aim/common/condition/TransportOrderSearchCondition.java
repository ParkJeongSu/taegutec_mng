package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransportOrderSearchCondition {
    private String transportOrderId;
    private Long idocId;
    private String description;
    private String carrierName;
    private String virtualCarrierName;
    private String transportType;
    private String transportStatus;
    private String lastTransactionCode;
    private String carrierType;
    private Integer priority;
    private String galId;
    private String galWarehouse;
    private String locationId;
    private String workStationId;
    private String sourceZoneName;
    private String destinationZoneName;
    private String errorText;
    private String actualWeight;
    private String requestedZoneName;
    private String actualZoneName;
    private String actualLocationId;
    private String travelProfile;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private LocalDateTime retrievalTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
}