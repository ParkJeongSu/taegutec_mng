package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TransportJobList {
    private String transportJobName;
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
    private String carrierType;
    private String transferState;

}