package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierLocationChangedBody {
    private String transportJobName;
    private String carrierName;
    private String carrierType;
    private String currentEquipmentName;
    private String currentPortName;
    private String currentZoneName;
    private String currentPositionType;
    private String currentPositionName;
    private String transferState;
    private String orderId;
    private String travelProfile;
    private String actualWeight;

}
