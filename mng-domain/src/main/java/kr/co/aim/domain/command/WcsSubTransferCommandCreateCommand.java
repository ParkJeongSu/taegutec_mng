package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferCommandCreateCommand {

    private TransactionInfo transactionInfo;

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
}
