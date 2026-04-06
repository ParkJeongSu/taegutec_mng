package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TransportJobCompletedBody {
    private String transportJobName;
    private String transportType;
    private String carrierName;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourcePositionTypeName;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationPositionTypeName;
    private String destinationPositionName;
    private String priority;
    private String orderId;
    private String requestSource;
    private String travelProfile;
    private String actualWeight;
    private String carrierType;
}