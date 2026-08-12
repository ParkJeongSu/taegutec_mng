package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.enums.RRNRequestState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductionOrder implements HasTransactionInfo {

    private Long id;
    private String orderId;
    private String orderLineNumber;
    private String lotName;
    private String description;
    private String itemName;
    private String recipeName;
    private String carrierName;
    private Long idocId;
    private Long h2OrderDpLineId;
    private String galKey;
    private Long mngKey;
    private String productionOrderType;
    private String productionOrderState;
    private String reportState;
    private String holdState;
    private String reasonCode;
    private String equipmentName;
    private BigDecimal planQuantity;
    private BigDecimal releasedQuantity;
    private BigDecimal startedQuantity;
    private BigDecimal endedQuantity;
    private BigDecimal scrappedQuantity;
    private String materialLotName;
    private String galOrderId;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private LocalDateTime validationTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
    private LocalDateTime dueDate;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static ProductionOrder create(ProductionOrderCreateCommand command) {
        return ProductionOrder
                .builder()
                .id(TsidUtils.nextId())
                .orderId(command.getOrderId())
                .orderLineNumber(command.getOrderLineNumber())
                .lotName(command.getLotName())
                .description(command.getDescription())
                .itemName(command.getItemName())
                .recipeName(command.getRecipeName())
                .carrierName(command.getCarrierName())
                .idocId(command.getIdocId())
                .h2OrderDpLineId(command.getH2OrderDpLineId())
                .galKey(command.getGalKey())
                .productionOrderType(command.getProductionOrderType())
                .productionOrderState(command.getProductionOrderState())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .equipmentName(command.getEquipmentName())
                .planQuantity(command.getPlanQuantity())
                .releasedQuantity(command.getReleasedQuantity())
                .startedQuantity(command.getStartedQuantity())
                .endedQuantity(command.getEndedQuantity())
                .scrappedQuantity(command.getScrappedQuantity())
                .materialLotName(command.getMaterialLotName())
                .galOrderId(command.getGalOrderId())
                .createTime(command.getCreateTime())
                .releaseTime(command.getReleaseTime())
                .completeTime(command.getCompleteTime())
                .validationTime(command.getValidationTime())
                .createUser(command.getCreateUser())
                .releaseUser(command.getReleaseUser())
                .completeUser(command.getCompleteUser())
                .dueDate(command.getDueDate())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public void change(ProductionOrderChangeCommand command){
        apply(command.getTransactionInfo());
        setProductionOrderState( ObjectUtils.isEmpty(command.getProductionOrderState()) ? getProductionOrderState() : command.getProductionOrderState());
        setH2OrderDpLineId(ObjectUtils.isEmpty(command.getH2OrderDpLineId()) ? getH2OrderDpLineId() : command.getH2OrderDpLineId());
        setGalKey(ObjectUtils.isEmpty(command.getGalKey()) ? getGalKey() :command.getGalKey());
        setMngKey(ObjectUtils.isEmpty(command.getMngKey()) ? getMngKey() :command.getMngKey());
        setItemName(ObjectUtils.isEmpty(command.getItemName()) ? getItemName() :command.getItemName());
        setRecipeName(ObjectUtils.isEmpty(command.getRecipeName()) ? getRecipeName() :command.getRecipeName());
        setCarrierName(ObjectUtils.isEmpty(command.getCarrierName()) ? getCarrierName() :command.getCarrierName());
        setIdocId(ObjectUtils.isEmpty(command.getIdocId()) ? getIdocId() :command.getIdocId());
        setH2OrderDpLineId(ObjectUtils.isEmpty(command.getH2OrderDpLineId()) ? getH2OrderDpLineId() :command.getH2OrderDpLineId());
        setGalKey(ObjectUtils.isEmpty(command.getGalKey()) ? getGalKey() :command.getGalKey());
        setMngKey(ObjectUtils.isEmpty(command.getMngKey()) ? getMngKey() :command.getMngKey());
        setProductionOrderType(ObjectUtils.isEmpty(command.getProductionOrderType()) ? getProductionOrderType() :command.getProductionOrderType());
        setProductionOrderState(ObjectUtils.isEmpty(command.getProductionOrderState()) ? getProductionOrderState() :command.getProductionOrderState());
        setReportState(ObjectUtils.isEmpty(command.getReportState()) ? getReportState() :command.getReportState());
        setHoldState(ObjectUtils.isEmpty(command.getHoldState()) ? getHoldState() :command.getHoldState());
        setReasonCode(ObjectUtils.isEmpty(command.getReasonCode()) ? getReasonCode() :command.getReasonCode());
        setEquipmentName(ObjectUtils.isEmpty(command.getEquipmentName()) ? getEquipmentName() :command.getEquipmentName());
        setPlanQuantity(ObjectUtils.isEmpty(command.getPlanQuantity()) ? getPlanQuantity() :command.getPlanQuantity());
        setReleasedQuantity(ObjectUtils.isEmpty(command.getReleasedQuantity()) ? getReleasedQuantity() :command.getReleasedQuantity());
        setStartedQuantity(ObjectUtils.isEmpty(command.getStartedQuantity()) ? getStartedQuantity() :command.getStartedQuantity());
        setEndedQuantity(ObjectUtils.isEmpty(command.getEndedQuantity()) ? getEndedQuantity() :command.getEndedQuantity());
        setScrappedQuantity(ObjectUtils.isEmpty(command.getScrappedQuantity()) ? getScrappedQuantity() :command.getScrappedQuantity());
        //setCreateTime(ObjectUtils.isEmpty(command.getCreateTime()) ? getCreateTime() :command.getCreateTime());
        setReleaseTime(ObjectUtils.isEmpty(command.getReleaseTime()) ? getReleaseTime() :command.getReleaseTime());
        setCompleteTime(ObjectUtils.isEmpty(command.getCompleteTime()) ? getCompleteTime() :command.getCompleteTime());
        setValidationTime(ObjectUtils.isEmpty(command.getValidationTime()) ? getValidationTime() :command.getValidationTime());
        setCreateUser(ObjectUtils.isEmpty(command.getCreateUser()) ? getCreateUser() :command.getCreateUser());
        setReleaseUser(ObjectUtils.isEmpty(command.getReleaseUser()) ? getReleaseUser() :command.getReleaseUser());
        setCompleteUser(ObjectUtils.isEmpty(command.getCompleteUser()) ? getCompleteUser() :command.getCompleteUser());
        setDueDate(ObjectUtils.isEmpty(command.getDueDate()) ? getDueDate() :command.getDueDate());

    }

    public void updateState(ProductionOrderUpdateStateCommand command){
        apply(command.getTransactionInfo());
        setProductionOrderState(command.getProductionOrderState());
    }

    public void processJobStarted(ProcessJobStartedCommand command){
        this.apply(command.getTransactionInfo());
        setCarrierName(command.getCarrierName());
        setReleasedQuantity(getReleasedQuantity().add(command.getQuantity()));
        setStartedQuantity(getStartedQuantity().add(command.getQuantity()));
        if(ObjectUtils.isEmpty(getReleaseTime())){
            setReleaseTime(command.getTransactionInfo().eventTime());
        }
    }

    public void processJobEnded(ProcessJobEndedCommand command){
        this.apply(command.getTransactionInfo());
        setCarrierName(command.getCarrierName());
        setReleasedQuantity(getReleasedQuantity().subtract(command.getQuantity()));
        setEndedQuantity(getEndedQuantity().add(command.getQuantity()));
        setCompleteTime(command.getTransactionInfo().eventTime());
    }

}
