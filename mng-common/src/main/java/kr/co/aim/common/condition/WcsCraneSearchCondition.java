package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCraneSearchCondition {

    private String factoryName;
    private String stockerName;
    private String craneName;
    private Integer craneNumber;
    private Integer localNo;
    private String status;
    private String autoRunStatus;
    private String carrierName;
    private String carrierExist;
    private String zoneName;
    private String forkStatus;
    private String forkPreStatus;
    private String mode;
    private String positionType;
    private String rowPosition;
    private String columnPosition;
    private String stagePosition;
    private String portNoPosition;
    private String errorHappen;
    private String jobCompleteState;
    private Boolean opportunisticEnabled;
}
