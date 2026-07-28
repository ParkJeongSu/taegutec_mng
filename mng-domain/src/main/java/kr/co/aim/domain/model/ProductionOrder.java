package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.enums.RRNRequestState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.command.ProductionOrderCreateCommand;
import kr.co.aim.domain.command.ProductionOrderUpdateStateCommand;
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
