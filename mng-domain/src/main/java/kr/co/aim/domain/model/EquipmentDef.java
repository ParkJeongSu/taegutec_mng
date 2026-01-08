package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.EquipmentDefCreateCommand;
import kr.co.aim.domain.command.EquipmentDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentDef implements HasTransactionInfo {
    private Long id;
    private String equipmentDefName;
    private String description;
    private String equipmentType;
    private Long equipmentGroupId;
    private String detailEquipmentType;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String containerType;

    public static EquipmentDef create(EquipmentDefCreateCommand command){
        return EquipmentDef.builder()
                .equipmentDefName(command.getEquipmentDefName())
                .description(command.getDescription())
                .equipmentType(command.getEquipmentType())
                .equipmentGroupId(command.getEquipmentGroupId())
                .detailEquipmentType(command.getDetailEquipmentType())
                .vendorId(command.getVendorId())
                .modelId(command.getModelId())
                .processCapacity(command.getProcessCapacity())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
    public void changeEquipmentDef(EquipmentDefUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDescription(command.getDescription());
        this.setEquipmentType(command.getEquipmentType());
        this.setEquipmentGroupId(command.getEquipmentGroupId());
        this.setDetailEquipmentType(command.getDetailEquipmentType());
        this.setVendorId(command.getVendorId());
        this.setModelId(command.getModelId());
        this.setProcessCapacity(command.getProcessCapacity());
    }
}
