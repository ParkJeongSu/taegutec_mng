package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.EquipmentGroupDefCreateCommand;
import kr.co.aim.domain.command.EquipmentGroupDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class EquipmentGroupDef implements HasTransactionInfo {

    private Long id;
    private String equipmentGroupName;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    public static EquipmentGroupDef create(EquipmentGroupDefCreateCommand command) {
        return EquipmentGroupDef.builder()
                .id(TsidUtils.nextId())
                .equipmentGroupName(command.getEquipmentGroupName())
                .description(command.getDescription())
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

    public EquipmentGroupDef update(EquipmentGroupDefUpdateCommand command) {
        this.setDescription(command.getDescription());
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
