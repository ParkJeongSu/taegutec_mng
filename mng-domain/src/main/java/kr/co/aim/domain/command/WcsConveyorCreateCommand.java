package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsConveyorCreateCommand {

    private TransactionInfo transactionInfo;

    private String conveyorGroup;
    private String conveyorName;
    private Integer conveyorNumber;
    private String factoryName;
    private Integer localNo;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private Integer conveyorGroupNumber;
    private String conveyorType;
    private String currentCmdData;
    private String direction;
    private String dispatchingPriority;
    private String errorHappen;
    private String jobCompleteState;
    private String onlineControlStatus;
    private String operationMode;
    private String preStatus;
    private Integer rtvNumber;
    private String status;
    private Integer touchPanelNumber;
    private String serverName;
    private String mode;
    private Integer downConveyorCount;
    private Integer onCarrierCount;
    private Integer totalConveyorCount;
    private Integer runConveyorCount;
    private String machineTypeName;
    private String readingEnableMode;
    private String rfidEnableMode;
    private String eqRouteKey;
    private String conveyorConnectionStatus;
    private String areaName;
}
