package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsCrane;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCraneResponse {

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

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsCraneResponse fromDomain(WcsCrane domain) {
        if (domain == null) {
            return null;
        }

        return WcsCraneResponse.builder()
                .craneName(domain.getCraneName())
                .craneNumber(domain.getCraneNumber())
                .factoryName(domain.getFactoryName())
                .localNo(domain.getLocalNo())
                .stockerName(domain.getStockerName())
                .autoRunStatus(domain.getAutoRunStatus())
                .carrierExist(domain.getCarrierExist())
                .carrierName(domain.getCarrierName())
                .columnPosition(domain.getColumnPosition())
                .currentCmdData(domain.getCurrentCmdData())
                .errorHappen(domain.getErrorHappen())
                .forkPreStatus(domain.getForkPreStatus())
                .forkStatus(domain.getForkStatus())
                .jobCompleteState(domain.getJobCompleteState())
                .mode(domain.getMode())
                .positionType(domain.getPositionType())
                .preStatus(domain.getPreStatus())
                .rowPosition(domain.getRowPosition())
                .stagePosition(domain.getStagePosition())
                .status(domain.getStatus())
                .touchPanelNumber(domain.getTouchPanelNumber())
                .zoneName(domain.getZoneName())
                .craneReadingEnableMode(domain.getCraneReadingEnableMode())
                .craneRfidEnableMode(domain.getCraneRfidEnableMode())
                .portNoPosition(domain.getPortNoPosition())
                .permissionRetryCount(domain.getPermissionRetryCount())
                .permissionRetryTime(domain.getPermissionRetryTime())
                .opportunisticEnabled(domain.getOpportunisticEnabled())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
