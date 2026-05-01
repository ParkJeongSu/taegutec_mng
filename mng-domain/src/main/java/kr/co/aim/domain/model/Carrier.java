package kr.co.aim.domain.model;
import jakarta.persistence.Column;
import kr.co.aim.common.enums.CarrierCleanState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Carrier implements HasTransactionInfo {
    private Long id;
    private String carrierName;
    private Long carrierDefId;
    private String carrierState;
    private String equipmentName;
    private String portName;
    private String zoneName;
    private String positionTypeName;
    private String positionName;
    private Integer capacity;
    private String cleanState;
    private String transportState;
    private String transportJobId;
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private String lotName;
    private String lotStatus;
    private String orderId;
    private String orderLineNumber;
    private String itemId;
    private Integer quantity;
    private String lastIdocId;
    private String interfaceStatus;
    private LocalDateTime interfaceRequestTime;
    private LocalDateTime interfaceReplyTime;
    private String equipmentFlag;
    private LocalDateTime jobEndTime;
    private LocalDateTime lastCleanTime;
    private LocalDateTime createTime;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String containerType;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Carrier fromHistory(CarrierHistory carrierHistory){
        return Carrier.builder()
                .id(carrierHistory.getId())
                .carrierName(carrierHistory.getCarrierName())
                .carrierDefId(carrierHistory.getCarrierDefId())
                .carrierState(carrierHistory.getCarrierState())
                .equipmentName(carrierHistory.getEquipmentName())
                .portName(carrierHistory.getPortName())
                .zoneName(carrierHistory.getZoneName())
                .capacity(carrierHistory.getCapacity())
                .cleanState(carrierHistory.getCleanState())
                .transportState(carrierHistory.getTransportState())
                .holdState(carrierHistory.getHoldState())
                .reasonCode(carrierHistory.getReasonCode())
                .useState(carrierHistory.getUseState())
                .useCount(carrierHistory.getUseCount())
                .useCountPerClean(carrierHistory.getUseCountPerClean())
                .cleanCount(carrierHistory.getCleanCount())
                .lastCleanTime(carrierHistory.getLastCleanTime())
                .createTime(carrierHistory.getCreateTime())
                .eventName(carrierHistory.getEventName())
                .eventTime(carrierHistory.getEventTime())
                .eventUser(carrierHistory.getEventUser())
                .eventComment(carrierHistory.getEventComment())
                .containerType(carrierHistory.getContainerType())
                .build();
    }

    public static Carrier create(CarrierCreateCommand command){
        return Carrier.builder()
                .carrierName(command.getCarrierName())
                .carrierDefId(command.getCarrierDefId())
                .carrierState(command.getCarrierState())
                .equipmentName(command.getEquipmentName())
                .portName(command.getPortName())
                .zoneName(command.getZoneName())
                .capacity(command.getCapacity())
                .cleanState(command.getCleanState())
                .transportState(command.getTransportState())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .useState(command.getUseState())
                .useCount(command.getUseCount())
                .useCountPerClean(command.getUseCountPerClean())
                .cleanCount(command.getCleanCount())
                .lastCleanTime(command.getLastCleanTime())
                .createTime(command.getCreateTime())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .containerType(command.getContainerType())
                .build();
    }

    public void deassigned(CarrierDeassignCommand command){
        this.apply(command.getTransactionInfo());
        setUseState(command.getUseState());
        setQuantity(command.getQuantity());
    }

    public void loadCompleted(LoadCompletedCommand command){
        this.apply(command.getTransactionInfo());
        setTransportState(command.getCarrierTransportState());
        setEquipmentName(command.getEquipmentName());
        setPortName(command.getPortName());
    }
    public void unloadRequest(UnLoadRequestCommand command){
        this.apply(command.getTransactionInfo());
        setTransportState(command.getCarrierTransportState());
        setEquipmentName(command.getEquipmentName());
        setPortName(command.getPortName());
    }
    public void cleanJobStarted(CleanJobStartedCommand command){
        this.apply(command.getTransactionInfo());
        setEquipmentName(command.getEquipmentName());
        setPortName(command.getPortName());
    }
    public void cleanJobEnded(CleanJobEndedCommand command){
        this.apply(command.getTransactionInfo());
        setEquipmentName(command.getEquipmentName());
        setPortName(command.getPortName());
        setCleanState(CarrierCleanState.CLEAN.getValue());
        setLastCleanTime(command.getTransactionInfo().eventTime());
        // TODO: ContainerType 컬럼을 Carriers 테이블의 추가 후 null 로 변경 로직 추가
    }
    public void locationChanged(LocationChangedCommand command){
        this.apply(command.getTransactionInfo());
        setEquipmentName(command.getEquipmentName());
        setPortName(command.getPortName());
        setZoneName(command.getZoneName());
        setPositionTypeName(command.getPositionType());
        setPositionName(command.getPositionName());
    }

}
