package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsTransferCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommandResponse {

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

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsTransferCommandResponse fromDomain(WcsTransferCommand domain) {
        if (domain == null) {
            return null;
        }

        return WcsTransferCommandResponse.builder()
                .transferCommandName(domain.getTransferCommandName())
                .carrierName(domain.getCarrierName())
                .commandStatus(domain.getCommandStatus())
                .createTime(domain.getCreateTime())
                .currentEquipmentName(domain.getCurrentEquipmentName())
                .hotLot(domain.getHotLot())
                .jobCompletedTime(domain.getJobCompletedTime())
                .jobReceiveTime(domain.getJobReceiveTime())
                .jobStartTime(domain.getJobStartTime())
                .lotName(domain.getLotName())
                .owner(domain.getOwner())
                .priority(domain.getPriority())
                .productQuantity(domain.getProductQuantity())
                .source(domain.getSource())
                .target(domain.getTarget())
                .targetEquipmentName(domain.getTargetEquipmentName())
                .factoryName(domain.getFactoryName())
                .currentSource(domain.getCurrentSource())
                .orderType(domain.getOrderType())
                .sourceEquipmentName(domain.getSourceEquipmentName())
                .sourceTransferType(domain.getSourceTransferType())
                .targetTransferType(domain.getTargetTransferType())
                .subCommandJobNo(domain.getSubCommandJobNo())
                .subCommandStatus(domain.getSubCommandStatus())
                .transferSpeed(domain.getTransferSpeed())
                .processType(domain.getProcessType())
                .startReportFlag(domain.getStartReportFlag())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
