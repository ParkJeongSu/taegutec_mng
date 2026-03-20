package kr.co.aim.domain.model;

import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.LoadCompletedCommand;
import kr.co.aim.domain.command.PortTypeChangedCommand;
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
    private String detailPortType;
    private String portUseType;
    private String workCenterName;
    private String locationId;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    public void portTypeChanged(PortTypeChangedCommand command){
        this.apply(command.getTransactionInfo());
        setPortType(command.getPortType().getValue());
    }

}
