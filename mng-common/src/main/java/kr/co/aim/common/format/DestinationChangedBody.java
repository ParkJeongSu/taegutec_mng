package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DestinationChangedBody {
    private String transportJobName;
    private String carrierName;
    
    private String oldDestinationEquipmentName;
    private String oldDestinationPortName;
    private String oldDestinationZoneName;
    private String oldDestinationPositionType;
    private String oldDestinationPositionName;

    private String newDestinationEquipmentName;
    private String newDestinationPortName;
    private String newDestinationZoneName;
    private String newDestinationPositionType;
    private String newDestinationPositionName;

    private String priority;
    private String carrierType;


}
