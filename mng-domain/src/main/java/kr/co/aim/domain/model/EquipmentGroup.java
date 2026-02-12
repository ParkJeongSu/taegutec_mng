package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.EquipmentGroupCreateCommand;
import kr.co.aim.domain.command.EquipmentGroupUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
@ToString
@Builder
public class EquipmentGroup implements HasTransactionInfo {

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
    public static EquipmentGroup create(EquipmentGroupCreateCommand command){
        return EquipmentGroup.builder()
                .equipmentGroupName(command.getEquipmentGroupName())
                .description(command.getDescription())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
    public void changeEquipmentGroup(EquipmentGroupUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDescription(command.getDescription());
    }
}
