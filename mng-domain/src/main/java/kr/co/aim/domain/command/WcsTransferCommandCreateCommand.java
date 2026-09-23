package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommandCreateCommand {

    private TransactionInfo transactionInfo;

    private String transferCommandName;
    private String carrierName;
    private String commandStatus;
    private LocalDateTime createTime;
    private String currentEquipmentName;
    private String hotLot;
    private LocalDateTime jobCompletedTime;
    private LocalDateTime jobReceiveTime;
    private LocalDateTime jobStartTime;
    private String lotName;
    private String owner;
    private Integer priority;
    private Integer productQuantity;
    private String source;
    private String target;
    private String targetEquipmentName;
    private String factoryName;
    private String currentSource;
    private String orderType;
    private String sourceEquipmentName;
    private String sourceTransferType;
    private String targetTransferType;
    private Integer subCommandJobNo;
    private String subCommandStatus;
    private Integer transferSpeed;
    private String processType;
    private Boolean startReportFlag;
}
