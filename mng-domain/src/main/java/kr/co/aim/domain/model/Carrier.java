package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.CarrierCleanState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Carrier implements HasTransactionInfo {
    private Long id;
    private String carrierName;
    private String carrierDefName;
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
    private BigDecimal quantity;
    private BigDecimal galQuantity;
    private LocalDateTime lastCleanTime;
    private LocalDateTime createTime;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String containerType;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Carrier create(CarrierCreateCommand command){
        return Carrier.builder()
                .id(TsidUtils.nextId())
                .carrierName(command.getCarrierName())
                .carrierDefName(command.getCarrierDefName())
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

    public void change(CarrierChangeCommand command){
        this.apply(command.getTransactionInfo());
        setCarrierDefName(ObjectUtils.isEmpty(command.getCarrierDefName()) ? getCarrierDefName() : command.getCarrierDefName());
        setCarrierState( ObjectUtils.isEmpty(command.getCarrierState()) ? getCarrierState() : command.getCarrierState());
        setEquipmentName( ObjectUtils.isEmpty(command.getEquipmentName()) ? getEquipmentName() : command.getEquipmentName());
        setPortName( ObjectUtils.isEmpty(command.getPortName()) ? getPortName() : command.getPortName());
        setZoneName( ObjectUtils.isEmpty(command.getZoneName()) ? getZoneName() : command.getZoneName());
        setPositionTypeName( ObjectUtils.isEmpty(command.getPositionTypeName()) ? getPositionTypeName() : command.getPositionTypeName());
        setPositionName( ObjectUtils.isEmpty(command.getPositionName()) ? getPositionName() : command.getPositionName());
        setCapacity( ObjectUtils.isEmpty(command.getCapacity()) ? getCapacity() : command.getCapacity());
        setCleanState( ObjectUtils.isEmpty(command.getCleanState()) ? getCleanState() : command.getCleanState());
        setTransportState( ObjectUtils.isEmpty(command.getTransportState()) ? getTransportState() : command.getTransportState());
        setTransportJobId( ObjectUtils.isEmpty(command.getTransportJobId()) ? getTransportJobId() : command.getTransportJobId());
        setHoldState( ObjectUtils.isEmpty(command.getHoldState()) ? getHoldState() : command.getHoldState());
        setReasonCode( ObjectUtils.isEmpty(command.getReasonCode()) ? getReasonCode() : command.getReasonCode());
        setUseState( ObjectUtils.isEmpty(command.getUseState()) ? getUseState() : command.getUseState());
        setUseCount( ObjectUtils.isEmpty(command.getUseCount()) ? getUseCount() : command.getUseCount());
        setUseCountPerClean( ObjectUtils.isEmpty(command.getUseCountPerClean()) ? getUseCountPerClean() : command.getUseCountPerClean());
        setCleanCount( ObjectUtils.isEmpty(command.getCleanCount()) ? getCleanCount() : command.getCleanCount());
        setQuantity( ObjectUtils.isEmpty(command.getQuantity()) ? getQuantity() : command.getQuantity());
        setGalQuantity( ObjectUtils.isEmpty(command.getGalQuantity()) ? getGalQuantity() : command.getGalQuantity());
        setLastCleanTime( ObjectUtils.isEmpty(command.getLastCleanTime()) ? getLastCleanTime() : command.getLastCleanTime());
        setCreateTime( ObjectUtils.isEmpty(command.getCreateTime()) ? getCreateTime() : command.getCreateTime());
        setInboundTime( ObjectUtils.isEmpty(command.getInboundTime()) ? getInboundTime() : command.getInboundTime());
        setOutboundTime( ObjectUtils.isEmpty(command.getOutboundTime()) ? getOutboundTime() : command.getOutboundTime());
        setContainerType( ObjectUtils.isEmpty(command.getContainerType()) ? getContainerType() : command.getContainerType());
    }

    public void deAssigned(CarrierDeassignCommand command){
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
