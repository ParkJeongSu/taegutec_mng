package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferCommandSearchCondition {

    private String transferCommandName;
    private Integer jobNo;
    private String carrierName;
    private String transferEquipmentName;
    private String subCommandStatus;
    private String transferType;
    private String factoryName;
    private String transferUnitName;
    private String jobCompleteState;
    private String sourcePositionName;
    private String targetPositionName;
    private String loadType;
}
