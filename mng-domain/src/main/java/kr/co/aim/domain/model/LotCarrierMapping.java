package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.MantiRequestState;
import kr.co.aim.common.enums.ProcessStatus;
import kr.co.aim.common.enums.ProductionStatus;
import kr.co.aim.common.enums.RRNRequestState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LotCarrierMapping implements HasTransactionInfo {

    private Long id;
    private String lotName;
    private String carrierName;
    private String orderId;
    private String orderLineNumber;
    private Long productionOrderId;
    private Integer seq;
    private String productionStatus;
    private String processStatus;
    private BigDecimal quantity;
    private BigDecimal galQuantity;
    private Long mngKey;
    private LocalDateTime validationTime;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private String mantiRequestState;
    private LocalDateTime mantiRequestTime;
    private LocalDateTime mantiReplyTime;
    private String rrnRequestState;
    private LocalDateTime rrnRequestTime;
    private LocalDateTime rrnReplyTime;
    private String nextEquipmentName;
    private String holdState;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static LotCarrierMapping create(LotCarrierMappingCreateCommand command) {
        return LotCarrierMapping.builder()
                .id(TsidUtils.nextId())
                .lotName(command.getLotName())
                .carrierName(command.getCarrierName())
                .orderId(command.getOrderId())
                .orderLineNumber(command.getOrderLineNumber())
                .productionOrderId(command.getProductionOrderId())
                .productionStatus(command.getProductionStatus())
                .processStatus(command.getProcessStatus())
                .quantity(command.getQuantity())
                .galQuantity(command.getGalQuantity())
                .mngKey(command.getMngKey())
                .jobStartTime(command.getJobStartTime())
                .jobEndTime(command.getJobEndTime())
                .mantiRequestState(command.getMantiRequestState())
                .mantiRequestTime(command.getMantiRequestTime())
                .mantiReplyTime(command.getMantiReplyTime())
                .rrnRequestState(command.getRrnRequestState())
                .rrnRequestTime(command.getRrnRequestTime())
                .rrnReplyTime(command.getRrnReplyTime())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public void allocated(AllocatedCommand command){
        this.apply(command.getTransactionInfo());
        setOrderId(command.getOrderId());
        setOrderLineNumber(command.getOrderLineNumber());
        setProductionOrderId(command.getProductionOrderId());
        setSeq(command.getSeq());
        setProductionStatus(command.getProductionStatus());
    }

    public void loadCompleted(LoadCompletedCommand command){
        this.apply(command.getTransactionInfo());
        setMngKey(TsidUtils.nextId());
        setMantiRequestState(MantiRequestState.WAIT.getValue());
        // port에 도착한 시간 기록
        setMantiRequestTime(command.getTransactionInfo().eventTime());
        setMantiReplyTime(null);
    }

    public void recipeRely(RecipeReplyCommand command){
        this.apply(command.getTransactionInfo());
        setMantiRequestState(MantiRequestState.COMPLETED.getValue());
        setMantiReplyTime(command.getTransactionInfo().eventTime());
    }

    public void recipeTimeOut(RecipeReplyCommand command){
        this.apply(command.getTransactionInfo());
        setMantiRequestState(MantiRequestState.TIMEOUT.getValue());
    }

    public Optional<LotCarrierMapping> deAssignedAndSplit(CarrierDeassignCommand command){
        this.apply(command.getTransactionInfo());
        if(Objects.equals(quantity, BigDecimal.ZERO)){
            setCarrierName(null);
            return Optional.empty();
        }
        else{
            setQuantity( getQuantity().subtract(command.getQuantity()));
            LotCarrierMapping newLotCarrierMapping = LotCarrierMapping.builder()
                    .id(TsidUtils.nextId())
                    .lotName(getLotName())
                    .carrierName(command.getCarrierName())
                    .orderId(getOrderId())
                    .orderLineNumber(getOrderLineNumber())
                    .productionStatus(ProductionStatus.WAIT.getValue())
                    .processStatus(ProcessStatus.WAIT.getValue())
                    .quantity(command.getQuantity())
                    .holdState(getHoldState())
                    .reasonCode(getReasonCode())
                    .eventName(command.getTransactionInfo().eventName())
                    .eventTime(command.getTransactionInfo().eventTime())
                    .eventUser(command.getTransactionInfo().eventUser())
                    .eventComment(command.getTransactionInfo().eventComment())
                    .build();

            return Optional.of(newLotCarrierMapping);
        }

    }

    public void processJobStarted(ProcessJobStartedCommand command){
        this.apply(command.getTransactionInfo());
        setJobStartTime(command.getTransactionInfo().eventTime());
        setProcessStatus(ProcessStatus.RUN.getValue());
        setProductionStatus(ObjectUtils.isEmpty(command.getProductionStatus()) ? getProductionStatus() : command.getProductionStatus());
    }

    public void processJobEnded(ProcessJobEndedCommand command){
        this.apply(command.getTransactionInfo());
        setQuantity(command.getQuantity());
        setJobEndTime(command.getTransactionInfo().eventTime());
        setProcessStatus(ProcessStatus.WAIT.getValue());
        setRrnRequestState(RRNRequestState.REQUESTED.getValue());
        setRrnRequestTime(command.getTransactionInfo().eventTime());
        setRrnReplyTime(null);
    }
}