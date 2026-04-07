package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CarrierUnBlockedBody {
    private String carrierName;
    private String currentEquipmentName;
    private String currentPortName;
    private String currentZoneName;
    private String currentPositionType;
    private String currentPositionName;
    private String travelProfile;
    private String actualWeight;
    private String carrierType;

}
