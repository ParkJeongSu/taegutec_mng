package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportJobCompletedBody {
    private String transportJobName;
    private String transportType;
    private String carrierName;
    private String sourceEquipmentName;
    private String sourceZoneName;
    private String sourcePositionTypeName;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationZoneName;
    private String destinationPositionTypeName;
    private String destinationPositionName;
    private String priority;
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