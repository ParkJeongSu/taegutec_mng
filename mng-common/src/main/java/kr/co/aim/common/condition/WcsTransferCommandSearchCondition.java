package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommandSearchCondition {

    private String transferCommandName;
    private String carrierName;
    private String factoryName;
    private String commandStatus;
    private String currentEquipmentName;
    private String hotLot;
    private String lotName;
    private String owner;
    private String source;
    private String target;
    private String targetEquipmentName;
    private String currentSource;
    private String orderType;
    private String sourceEquipmentName;
    private String sourceTransferType;
    private String targetTransferType;
    private Integer subCommandJobNo;
    private String subCommandStatus;
    private String processType;
    private Boolean startReportFlag;
}
