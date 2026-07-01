package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.PortDefCreateCommand;
import kr.co.aim.domain.command.PortDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PortDef implements HasTransactionInfo {

    private Long id;
    private String equipmentName;
    private String portName;
    private String factoryName;
    private Integer portNumber;
    private String description;
    private String transportMode;
    private String portType;
    private String detailPortType;
    private String portUseType;
    private String portRoleType;
    private String workCenterName;
    private String locationId;
    private String connectedEquipmentName;
    private String connectedPortName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static PortDef create(PortDefCreateCommand command) {
        return PortDef.builder()
                .id(TsidUtils.nextId())
                .equipmentName(command.getEquipmentName())
                .portName(command.getPortName())
                .factoryName(command.getFactoryName())
                .portNumber(command.getPortNumber())
                .description(command.getDescription())
                .transportMode(command.getTransportMode())
                .portType(command.getPortType())
                .detailPortType(command.getDetailPortType())
                .portUseType(command.getPortUseType())
                .portRoleType(command.getPortRoleType())
                .workCenterName(command.getWorkCenterName())
                .locationId(command.getLocationId())
                .connectedEquipmentName(command.getConnectedEquipmentName())
                .connectedPortName(command.getConnectedPortName())
                .checkOutState(command.getCheckOutState())
                .checkOutTime(command.getCheckOutTime())
                .checkOutUser(command.getCheckOutUser())
                .dataState(command.getDataState())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public PortDef update(PortDefUpdateCommand command) {
        this.setFactoryName(command.getFactoryName());
        this.setPortNumber(command.getPortNumber());
        this.setDescription(command.getDescription());
        this.setTransportMode(command.getTransportMode());
        this.setPortType(command.getPortType());
        this.setDetailPortType(command.getDetailPortType());
        this.setPortUseType(command.getPortUseType());
        this.setPortRoleType(command.getPortRoleType());
        this.setWorkCenterName(command.getWorkCenterName());
        this.setLocationId(command.getLocationId());
        this.setConnectedEquipmentName(command.getConnectedEquipmentName());
        this.setConnectedPortName(command.getConnectedPortName());
        this.setCheckOutState(command.getCheckOutState());
        this.setCheckOutTime(command.getCheckOutTime());
        this.setCheckOutUser(command.getCheckOutUser());
        this.setDataState(command.getDataState());
        this.setEventName(command.getTransactionInfo().eventName());
        this.setEventTime(command.getTransactionInfo().eventTime());
        this.setEventUser(command.getTransactionInfo().eventUser());
        this.setEventComment(command.getTransactionInfo().eventComment());
        return this;
    }
}
