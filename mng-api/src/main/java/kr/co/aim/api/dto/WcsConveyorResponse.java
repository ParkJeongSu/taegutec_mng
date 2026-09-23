package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsConveyor;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsConveyorResponse {

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

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsConveyorResponse fromDomain(WcsConveyor domain) {
        if (domain == null) {
            return null;
        }

        return WcsConveyorResponse.builder()
                .conveyorGroup(domain.getConveyorGroup())
                .conveyorName(domain.getConveyorName())
                .conveyorNumber(domain.getConveyorNumber())
                .factoryName(domain.getFactoryName())
                .localNo(domain.getLocalNo())
                .autoRunStatus(domain.getAutoRunStatus())
                .carrierExist(domain.getCarrierExist())
                .carrierName(domain.getCarrierName())
                .conveyorGroupNumber(domain.getConveyorGroupNumber())
                .conveyorType(domain.getConveyorType())
                .currentCmdData(domain.getCurrentCmdData())
                .direction(domain.getDirection())
                .dispatchingPriority(domain.getDispatchingPriority())
                .errorHappen(domain.getErrorHappen())
                .jobCompleteState(domain.getJobCompleteState())
                .onlineControlStatus(domain.getOnlineControlStatus())
                .operationMode(domain.getOperationMode())
                .preStatus(domain.getPreStatus())
                .rtvNumber(domain.getRtvNumber())
                .status(domain.getStatus())
                .touchPanelNumber(domain.getTouchPanelNumber())
                .serverName(domain.getServerName())
                .mode(domain.getMode())
                .downConveyorCount(domain.getDownConveyorCount())
                .onCarrierCount(domain.getOnCarrierCount())
                .totalConveyorCount(domain.getTotalConveyorCount())
                .runConveyorCount(domain.getRunConveyorCount())
                .machineTypeName(domain.getMachineTypeName())
                .readingEnableMode(domain.getReadingEnableMode())
                .rfidEnableMode(domain.getRfidEnableMode())
                .eqRouteKey(domain.getEqRouteKey())
                .conveyorConnectionStatus(domain.getConveyorConnectionStatus())
                .areaName(domain.getAreaName())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
