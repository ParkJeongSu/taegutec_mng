package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportJobCancelStartedBody {
    private String transportJobName;
    private String transportType;
    private String carrierName;
    private String currentEquipmentName;
    private String currentZoneName;
    private String currentPositionTypeName;
    private String currentPositionName;
    private String orderId;
    private String orderLineNumber;
    private String productionType;
    private String lotName;
    private String itemName;
    private String requestSource;
    private String travelProfile;
    private String actualWeight;
    private String carrierType;
}