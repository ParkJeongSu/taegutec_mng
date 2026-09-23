package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCraneCreateCommand {

    private TransactionInfo transactionInfo;

    private String craneName;
    private Integer craneNumber;
    private String factoryName;
    private Integer localNo;
    private String stockerName;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private String columnPosition;
    private String currentCmdData;
    private String errorHappen;
    private String forkPreStatus;
    private String forkStatus;
    private String jobCompleteState;
    private String mode;
    private String positionType;
    private String preStatus;
    private String rowPosition;
    private String stagePosition;
    private String status;
    private Integer touchPanelNumber;
    private String zoneName;
    private String craneReadingEnableMode;
    private String craneRfidEnableMode;
    private String portNoPosition;
    private Integer permissionRetryCount;
    private Integer permissionRetryTime;
    private Boolean opportunisticEnabled;
}
