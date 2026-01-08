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
    private String oldDestinationPositionType;
    private String oldDestinationPositionName;
    private String oldDestinationZoneName;

    private String newDestinationEquipmentName;
    private String newDestinationPositionType;
    private String newDestinationPositionName;
    private String newDestinationZoneName;


}
