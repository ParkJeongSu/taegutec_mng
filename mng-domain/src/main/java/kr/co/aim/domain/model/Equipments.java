package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Equipments implements HasTransactionInfo {
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
    private Long workOrderId;

    public static Equipments create(EquipmentsCreateCommand command){
        return Equipments.builder()
                .equipmentName(command.getEquipmentName())
                .equipmentDefId(command.getEquipmentDefId())
                .parentEquipmentId(command.getParentEquipmentId())
                .equipmentLevel(command.getEquipmentLevel())
                .equipmentState(command.getEquipmentState())
                .communicationState(command.getCommunicationState())
                .processCount(command.getProcessCount())
                .recipeName(command.getRecipeName())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .resourceState(command.getResourceState())
                .operationMode(command.getOperationMode())
                .messageServiceAddress(command.getMessageServiceAddress())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
    public void changeEquipment(EquipmentsUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setEquipmentDefId(command.getEquipmentDefId());
        this.setParentEquipmentId(command.getParentEquipmentId());
        this.setEquipmentLevel(command.getEquipmentLevel());
        this.setEquipmentState(command.getEquipmentState());
        this.setCommunicationState(command.getCommunicationState());
        this.setProcessCount(command.getProcessCount());
        this.setRecipeName(command.getRecipeName());
        this.setHoldState(command.getHoldState());
        this.setReasonCode(command.getReasonCode());
        this.setResourceState(command.getResourceState());
        this.setOperationMode(command.getOperationMode());
        this.setMessageServiceAddress(command.getMessageServiceAddress());
    }


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
