package kr.co.aim.domain.model;

import kr.co.aim.common.enums.PortTransportState;
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
public class Ports implements HasTransactionInfo {
    private Long id;
    private String equipmentName;
    private String portName;
    private String description;
    private String connectedStocker;
    private String transportMode;
    private String portState;
    private String resourceState;
    private String transportState;
    private String carrierName;
    private Long transportJobId;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Ports create(PortsCreateCommand command){
        return Ports.builder()
                .equipmentName(command.getEquipmentName())
                .portName(command.getPortName())
                .description(command.getDescription())
                .connectedStocker(command.getConnectedStocker())
                .transportMode(command.getTransportMode())
                .portState(command.getPortState())
                .resourceState(command.getResourceState())
                .transportState(command.getTransportState())
                .carrierName(command.getCarrierName())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
    public void changePort(PortsUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDescription(command.getDescription());
        this.setConnectedStocker(command.getConnectedStocker());
        this.setTransportMode(command.getTransportMode());
        this.setPortState(command.getPortState());
        this.setResourceState(command.getResourceState());
        this.setTransportState(command.getTransportState());
        this.setCarrierName(command.getCarrierName());
    }


    public void loadRequest(LoadRequestCommand command){
        this.apply(command.getTransactionInfo());
        setTransportState(PortTransportState.READY_TO_LOAD.getValue());
    }

    public void loadCompleted(LoadCompletedCommand command){
        this.apply(command.getTransactionInfo());
        setCarrierName(command.getCarrierName());
        setTransportState(PortTransportState.READY_TO_PROCESS.getValue());
    }

    public void unloadRequest(UnLoadRequestCommand command){
        this.apply(command.getTransactionInfo());
        setTransportState(PortTransportState.READY_TO_UNLOAD.getValue());
    }

    public void transportModeChanged(PortTransportModeChangedCommand command){
        this.apply(command.getTransactionInfo());
        setTransportMode(command.getPortTransportModeName());
    }
    public void portStateChanged(PortStateChangedCommand command){
        this.apply(command.getTransactionInfo());
        setPortState(command.getPortState().name());
    }
}
