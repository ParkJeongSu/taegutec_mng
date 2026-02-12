package kr.co.aim.domain.model;

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
public class Carriers implements HasTransactionInfo {
    private Long id;
    private String carrierName;
    private Long carrierDefId;
    private String carrierState;
    private String equipmentName;
    private String portName;
    private String zoneName;
    private String shelfName;
    private Integer capacity;
    private String cleanState;
    private String transportState; // [ InEQP | OnPort | InWareHouse | InArea | Moving ]
    private String reservedObjectId;
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private Integer lotQuantity;
    private Integer quantity;
    private String capaState;
    private LocalDateTime lastCleanTime;
    private LocalDateTime createTime;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String containerType;

    public static Carriers create(CarriersCreateCommand command){
        return Carriers.builder()
                .carrierName(command.getCarrierName())
                .carrierDefId(command.getCarrierDefId())
                .carrierState(command.getCarrierState())
                .equipmentName(command.getEquipmentName())
                .portName(command.getPortName())
                .zoneName(command.getZoneName())
                .shelfName(command.getShelfName())
                .capacity(command.getCapacity())
                .cleanState(command.getCleanState())
                .transportState(command.getTransportState())
                .reservedObjectId(command.getReservedObjectId())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .useState(command.getUseState())
                .useCount(command.getUseCount())
                .useCountPerClean(command.getUseCountPerClean())
                .cleanCount(command.getCleanCount())
                .lotQuantity(command.getLotQuantity())
                .capaState(command.getCapaState())
                .lastCleanTime(command.getLastCleanTime())
                .createTime(command.getCreateTime())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .containerType(command.getContainerType())
                .build();
    }
    public void changeCarriers(CarriersUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setCarrierName(command.getCarrierName());
        this.setCarrierDefId(command.getCarrierDefId());
        this.setCarrierState(command.getCarrierState());
        this.setEquipmentName(command.getEquipmentName());
        this.setPortName(command.getPortName());
        this.setZoneName(command.getZoneName());
        this.setShelfName(command.getShelfName());
        this.setCapacity(command.getCapacity());
        this.setCleanState(command.getCleanState());
        this.setTransportState(command.getTransportState());
        this.setReservedObjectId(command.getReservedObjectId());
        this.setHoldState(command.getHoldState());
        this.setReasonCode(command.getReasonCode());
        this.setUseState(command.getUseState());
        this.setUseCount(command.getUseCount());
        this.setUseCountPerClean(command.getUseCountPerClean());
        this.setCleanCount(command.getCleanCount());
        this.setLotQuantity(command.getLotQuantity());
        this.setCapaState(command.getCapaState());
        this.setLastCleanTime(command.getLastCleanTime());
        this.setCreateTime(command.getCreateTime());
        this.setContainerType(command.getContainerType());
    }


    public void deassigned(CarriersDeassignCommand command){
        this.apply(command.getTransactionInfo());
        setUseState(command.getUseState());
        setLotQuantity(command.getLotQuantity());
        setQuantity(command.getQuantity());
        setCapaState(command.getCapaState());
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
        // TODO: zoneName이나.. 위치정보의 값을 어떻게 구상할지 고민
    }

}
