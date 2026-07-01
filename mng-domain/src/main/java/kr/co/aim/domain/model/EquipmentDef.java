package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.EquipmentDefCreateCommand;
import kr.co.aim.domain.command.EquipmentDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentDef implements HasTransactionInfo {

    private Long id;
    private String equipmentName;
    private String factoryName;
    private String description;
    private String equipmentType;
    private String equipmentGroupName;
    private String detailEquipmentType;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private String containerType;
    private String plcType;
    private String routeKey;
    private String serverName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private Integer localNo;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static EquipmentDef create(EquipmentDefCreateCommand command) {
        return EquipmentDef.builder()
                .id(TsidUtils.nextId())
                .equipmentName(command.getEquipmentName())
                .factoryName(command.getFactoryName())
                .description(command.getDescription())
                .equipmentType(command.getEquipmentType())
                .equipmentGroupName(command.getEquipmentGroupName())
                .detailEquipmentType(command.getDetailEquipmentType())
                .vendorId(command.getVendorId())
                .modelId(command.getModelId())
                .processCapacity(command.getProcessCapacity())
                .containerType(command.getContainerType())
                .plcType(command.getPlcType())
                .routeKey(command.getRouteKey())
                .serverName(command.getServerName())
                .checkOutState(command.getCheckOutState())
                .checkOutTime(command.getCheckOutTime())
                .checkOutUser(command.getCheckOutUser())
                .dataState(command.getDataState())
                .localNo(command.getLocalNo())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public EquipmentDef update(EquipmentDefUpdateCommand command) {
        this.setFactoryName(command.getFactoryName());
        this.setDescription(command.getDescription());
        this.setEquipmentType(command.getEquipmentType());
        this.setEquipmentGroupName(command.getEquipmentGroupName());
        this.setDetailEquipmentType(command.getDetailEquipmentType());
        this.setVendorId(command.getVendorId());
        this.setModelId(command.getModelId());
        this.setProcessCapacity(command.getProcessCapacity());
        this.setContainerType(command.getContainerType());
        this.setPlcType(command.getPlcType());
        this.setRouteKey(command.getRouteKey());
        this.setServerName(command.getServerName());
        this.setCheckOutState(command.getCheckOutState());
        this.setCheckOutTime(command.getCheckOutTime());
        this.setCheckOutUser(command.getCheckOutUser());
        this.setDataState(command.getDataState());
        this.setLocalNo(command.getLocalNo());
        this.setEventName(command.getTransactionInfo().eventName());
        this.setEventTime(command.getTransactionInfo().eventTime());
        this.setEventUser(command.getTransactionInfo().eventUser());
        this.setEventComment(command.getTransactionInfo().eventComment());
        return this;
    }

}
