package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.DestinationChangedCommand;
import kr.co.aim.domain.command.TransportJobDetailCreateCommand;
import kr.co.aim.domain.command.TransportJobDetailUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportJobDetail implements HasTransactionInfo {
    private Long id;
    private String transportJobDetailName;
    private Long transportJobId;
    private String transportJobDetailState;
    private String carrierId;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourceShelfName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationShelfName;
    private String currentEquipmentName;
    private String currentPortName;
    private String currentZoneName;
    private String currentShelfName;
    private Integer stepOrder;
    private Integer stepPhase;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static TransportJobDetail create(TransportJobDetailCreateCommand command){
        return TransportJobDetail.builder()
                .transportJobDetailName(command.getTransportJobDetailName())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeTransportJobDetail(TransportJobDetailUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }

    public void destinationChanged(DestinationChangedCommand command){
        this.apply(command.getTransactionInfo());
        setDestinationEquipmentName(command.getNewDestinationEquipmentName());
        setDestinationPortName(command.getNewDestinationPositionName());
        setDestinationZoneName(command.getNewDestinationZoneName());
    }
}
