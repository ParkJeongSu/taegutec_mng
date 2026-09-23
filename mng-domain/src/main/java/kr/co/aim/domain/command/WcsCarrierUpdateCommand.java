package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCarrierUpdateCommand {

    private TransactionInfo transactionInfo;

    private String afterProcess;
    private String beforeProcess;
    private String carrierGroup;
    private String carrierStatus;
    private LocalDateTime createTime;
    private String currentPositionName;
    private String hotLot;
    private String lotName;
    private String owner;
    private String previousCarrierStatus;
    private Integer productQuantity;
    private String zoneName;
    private String currentEquipmentName;
    private String carrierDetailType;
    private String carrierType;
    private String transferCommandName;
    private String travelProfile;
    private String itemName;
    private String orderId;
    private String orderLineNumber;
    private String productionType;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private Double weight;
    private Integer carrierUseCount;
}
