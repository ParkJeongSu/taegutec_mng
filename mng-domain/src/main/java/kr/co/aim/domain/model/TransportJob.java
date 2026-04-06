package kr.co.aim.domain.model;
import jakarta.persistence.Column;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.command.TransportJobUpdateCommand;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
    private String transportType; // I O R
    private String transportJobState;
    private String carrierType;
    private String travelProfile;
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
    private String requestSource;
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
                .transportType(command.getTransportType())
                .transportJobState(command.getTransportJobState())
                .carrierType(command.getCarrierType())
                .travelProfile(command.getTravelProfile())
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
                .requestSource(command.getRequestSource())
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
        this.setTransportJobState(StringUtils.isNotBlank(command.getTransportJobState())?command.getTransportJobState():getTransportJobState());
        this.setCarrierName(StringUtils.isNotBlank(command.getCarrierName())?command.getCarrierName():getCarrierName());
        this.setTransportType(StringUtils.isNotBlank(command.getTransportType())?command.getTransportType():getTransportType());
        this.setTransportJobState(StringUtils.isNotBlank(command.getTransportJobState())?command.getTransportJobState():getTransportJobState());
        this.setCarrierType(StringUtils.isNotBlank(command.getCarrierType())?command.getCarrierType():getCarrierType());
        this.setTravelProfile(StringUtils.isNotBlank(command.getTravelProfile())?command.getTravelProfile():getTravelProfile());
        this.setSourceEquipmentName(StringUtils.isNotBlank(command.getSourceEquipmentName())?command.getSourceEquipmentName():getSourceEquipmentName());
        this.setSourcePortName(StringUtils.isNotBlank(command.getSourcePortName())?command.getSourcePortName():getSourcePortName());
        this.setSourceZoneName(StringUtils.isNotBlank(command.getSourceZoneName())?command.getSourceZoneName():getSourceZoneName());
        this.setSourcePositionTypeName(StringUtils.isNotBlank(command.getSourcePositionTypeName())?command.getSourcePositionTypeName():getSourcePositionTypeName());
        this.setSourcePositionName(StringUtils.isNotBlank(command.getSourcePositionName())?command.getSourcePositionName():getSourcePositionName());
        this.setDestinationEquipmentName(StringUtils.isNotBlank(command.getDestinationEquipmentName())?command.getDestinationEquipmentName():getDestinationEquipmentName());
        this.setDestinationPortName(StringUtils.isNotBlank(command.getDestinationPortName())?command.getDestinationPortName():getDestinationPortName());
        this.setDestinationZoneName(StringUtils.isNotBlank(command.getDestinationZoneName())?command.getDestinationZoneName():getDestinationZoneName());
        this.setDestinationPositionTypeName(StringUtils.isNotBlank(command.getDestinationPositionTypeName())?command.getDestinationPositionTypeName():getDestinationPositionTypeName());
        this.setDestinationPositionName(StringUtils.isNotBlank(command.getDestinationPositionName())?command.getDestinationPositionName():getDestinationPositionName());
        this.setPriority(ObjectUtils.isNotEmpty(command.getPriority()) ?command.getPriority():getPriority());
        this.setErrorCode(StringUtils.isNotBlank(command.getErrorCode())?command.getErrorCode():getErrorCode());
        this.setErrorText(StringUtils.isNotBlank(command.getErrorText())?command.getErrorText():getErrorText());
        this.setRequestSource(StringUtils.isNotBlank(command.getRequestSource())?command.getRequestSource():getRequestSource());
        this.setDepartedTime(ObjectUtils.isNotEmpty(command.getDepartedTime())?command.getDepartedTime():getDepartedTime());
        this.setArrivedTime(ObjectUtils.isNotEmpty(command.getArrivedTime())?command.getArrivedTime():getArrivedTime());
        this.setReasonCode(StringUtils.isNotBlank(command.getReasonCode())?command.getReasonCode():getReasonCode());
        this.setOrderId(StringUtils.isNotBlank(command.getOrderId())?command.getOrderId():getOrderId());
    }

    public void changeDestination(TransportJobUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setDestinationEquipmentName(StringUtils.isNotBlank(command.getDestinationEquipmentName())?command.getDestinationEquipmentName():getDestinationEquipmentName());
        this.setDestinationPortName(StringUtils.isNotBlank(command.getDestinationPortName())?command.getDestinationPortName():getDestinationPortName());
        this.setDestinationZoneName(StringUtils.isNotBlank(command.getDestinationZoneName())?command.getDestinationZoneName():getDestinationZoneName());
        this.setDestinationPositionTypeName(StringUtils.isNotBlank(command.getDestinationPositionTypeName())?command.getDestinationPositionTypeName():getDestinationPositionTypeName());
        this.setDestinationPositionName(StringUtils.isNotBlank(command.getDestinationPositionName())?command.getDestinationPositionName():getDestinationPositionName());
    }


}
