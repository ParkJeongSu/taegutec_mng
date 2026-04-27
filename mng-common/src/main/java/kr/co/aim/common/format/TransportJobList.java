package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportJobList {
    private String transportJobName;
    private String transportType;
    private String carrierName;
    private String sourceEquipmentName;
    private String sourcePositionType;
    private String sourcePositionName;
    private String sourceZoneName;

    private String currentEquipmentName;
    private String currentPositionType;
    private String currentPositionName;
    private String currentZoneName;

    private String destinationEquipmentName;
    private String destinationPositionType;
    private String destinationPositionName;
    private String destinationZoneName;

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