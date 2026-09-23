package kr.co.aim.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferCommandUpdateRequestDto {

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

    private String eventName;
    private String eventUser;
    private String eventComment;
}
