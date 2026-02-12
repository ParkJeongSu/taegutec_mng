package kr.co.aim.domain.model;

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
    private String description;
    private String portType;
    private String portUseType;
    private String containerType;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static PortDef create(PortDefCreateCommand command){
        return PortDef.builder()
                .equipmentName("")
                .portName("")
                .description(command.getDescription())
                .portType(command.getPortType())
                .portUseType(command.getPortUseType())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public void changePortDef(PortDefUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDescription(command.getDescription());
        this.setPortType(command.getPortType());
        this.setPortUseType(command.getPortUseType());
    }
}
