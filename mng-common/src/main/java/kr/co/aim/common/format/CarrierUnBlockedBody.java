package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
