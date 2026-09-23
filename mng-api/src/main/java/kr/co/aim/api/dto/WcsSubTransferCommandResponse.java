package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsSubTransferCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferCommandResponse {

    private Integer jobNo;
    private String transferCommandName;

    private LocalDateTime createTime;
    private String currentCommandData;
    private String jobCompleteState;
    private Integer localNo;
    private String sourcePositionColumn;
    private String sourcePositionName;
    private String sourcePositionPortNo;
    private String sourcePositionRow;
    private String sourcePositionStage;
    private String sourcePositionBin;
    private String subCommandStatus;
    private String targetPositionColumn;
    private String targetPositionName;
    private String targetPositionPortNo;
    private String targetPositionRow;
    private String targetPositionStage;
    private String targetPositionBin;
    private String transferEquipmentName;
    private String transferType;
    private String transferUnitName;
    private Integer transferUnitNumber;
    private String factoryName;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private Integer transferSpeed;
    private String loadType;
    private String carrierName;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsSubTransferCommandResponse fromDomain(WcsSubTransferCommand domain) {
        if (domain == null) {
            return null;
        }

        return WcsSubTransferCommandResponse.builder()
                .jobNo(domain.getJobNo())
                .transferCommandName(domain.getTransferCommandName())
                .createTime(domain.getCreateTime())
                .currentCommandData(domain.getCurrentCommandData())
                .jobCompleteState(domain.getJobCompleteState())
                .localNo(domain.getLocalNo())
                .sourcePositionColumn(domain.getSourcePositionColumn())
                .sourcePositionName(domain.getSourcePositionName())
                .sourcePositionPortNo(domain.getSourcePositionPortNo())
                .sourcePositionRow(domain.getSourcePositionRow())
                .sourcePositionStage(domain.getSourcePositionStage())
                .sourcePositionBin(domain.getSourcePositionBin())
                .subCommandStatus(domain.getSubCommandStatus())
                .targetPositionColumn(domain.getTargetPositionColumn())
                .targetPositionName(domain.getTargetPositionName())
                .targetPositionPortNo(domain.getTargetPositionPortNo())
                .targetPositionRow(domain.getTargetPositionRow())
                .targetPositionStage(domain.getTargetPositionStage())
                .targetPositionBin(domain.getTargetPositionBin())
                .transferEquipmentName(domain.getTransferEquipmentName())
                .transferType(domain.getTransferType())
                .transferUnitName(domain.getTransferUnitName())
                .transferUnitNumber(domain.getTransferUnitNumber())
                .factoryName(domain.getFactoryName())
                .endTime(domain.getEndTime())
                .startTime(domain.getStartTime())
                .transferSpeed(domain.getTransferSpeed())
                .loadType(domain.getLoadType())
                .carrierName(domain.getCarrierName())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
