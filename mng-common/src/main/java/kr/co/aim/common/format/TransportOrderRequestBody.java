package kr.co.aim.common.format;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportOrderRequestBody {
    private Long id;
    private String transportOrderId;
    private Long idocId;
    private String description;
    private String carrierName;
    private String transportType;
    private String transportStatus;
    private String lastTransactionCode;
    private String carrierType;
    private Integer priority;
    private Long galId;
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
    private String drivingProfile;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private LocalDateTime retrievalTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}