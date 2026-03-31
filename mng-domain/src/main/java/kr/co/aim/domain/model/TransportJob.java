package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.command.TransportJobUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportJob implements HasTransactionInfo {

    private Long id;
    private String transportJobName;
    private String carrierName;
    private String transportJobState;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourcePositionTypeName;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationPositionTypeName;
    private String destinationPositionName;
    private Integer priority;
    private String errorCode;
    private String errorText;
    private String requestType;
    private LocalDateTime createTime;
    private LocalDateTime departedTime;
    private LocalDateTime arrivedTime;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String orderId;

    public static TransportJob create(TransportJobCreateCommand command){
        return TransportJob.builder()
                .id(TsidUtils.nextId())
                .transportJobName(command.getTransportJobName())
                .carrierName(command.getCarrierName())
                .transportJobState(command.getTransportJobState())
                .sourceEquipmentName(command.getSourceEquipmentName())
                .sourcePortName(command.getSourcePortName())
                .sourceZoneName(command.getSourceZoneName())
                .sourcePositionTypeName(command.getSourcePositionTypeName())
                .sourcePositionName(command.getSourcePositionName())
                .destinationEquipmentName(command.getDestinationEquipmentName())
                .destinationPortName(command.getDestinationPortName())
                .destinationZoneName(command.getDestinationZoneName())
                .destinationPositionTypeName(command.getDestinationPositionTypeName())
                .destinationPositionName(command.getDestinationPositionName())
                .priority(command.getPriority())
                .errorCode(command.getErrorCode())
                .errorText(command.getErrorText())
                .requestType(command.getRequestType())
                .createTime(command.getCreateTime())
                .departedTime(command.getDepartedTime())
                .arrivedTime(command.getArrivedTime())
                .reasonCode(command.getReasonCode())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .orderId(command.getOrderId())
                .build();
    }
    public void changeTransportJob(TransportJobUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setTransportJobState(command.getTransportJobState());
    }
}
