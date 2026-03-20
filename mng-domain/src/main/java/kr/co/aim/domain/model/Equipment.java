package kr.co.aim.domain.model;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Equipment implements HasTransactionInfo {
    private Long id;
    private String equipmentName;
    private Long equipmentDefId;
    private Long parentEquipmentId;
    private String equipmentLevel;
    private String equipmentState;
    private String communicationState;
    private Integer loadingCount;
    private Integer processCount;
    private String recipeName;
    private String holdState;
    private String reasonCode;
    private String resourceState;
    private String operationMode;
    private String messageServiceAddress;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private Long productionOrderId;

    public void communicationStateChange(CommunicationStateChangeCommand communicationStateChangeCommand){
        setCommunicationState(communicationStateChangeCommand.getCommunicationState().name());
        this.apply(communicationStateChangeCommand.getTransactionInfo());
    }

    public void equipmentStateChange(EquipmentStateChangeCommand equipmentStateChangeCommand){
        setEquipmentState(equipmentStateChangeCommand.getEquipmentState().name());
        this.apply(equipmentStateChangeCommand.getTransactionInfo());
    }

    public void operationModeChange(EquipmentOperationModeChangeCommand equipmentOperationModeChangeCommand){
        setOperationMode(equipmentOperationModeChangeCommand.getEquipmentOperationMode().name());
        this.apply(equipmentOperationModeChangeCommand.getTransactionInfo());
    }

}
