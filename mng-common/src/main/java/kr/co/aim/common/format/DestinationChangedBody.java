package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationChangedBody {
    private String transportJobName;
    private String carrierName;
    
    private String oldDestinationEquipmentName;
    private String oldDestinationZoneName;
    private String oldDestinationPositionType;
    private String oldDestinationPositionName;

    private String newDestinationEquipmentName;
    private String newDestinationZoneName;
    private String newDestinationPositionType;
    private String newDestinationPositionName;

    private String priority;
    private String carrierType;


}
