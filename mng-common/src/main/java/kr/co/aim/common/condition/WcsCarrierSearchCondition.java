package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCarrierSearchCondition {

    private String factoryName;
    private String carrierName;
    private String carrierStatus;
    private String carrierType;
    private String carrierDetailType;
    private String carrierGroup;
    private String currentPositionName;
    private String currentEquipmentName;
    private String zoneName;
    private String lotName;
    private String orderId;
    private String itemName;
    private String productionType;
}
