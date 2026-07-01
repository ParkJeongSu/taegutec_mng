package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.MantiRequestState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.LoadCompletedCommand;
import kr.co.aim.domain.command.LotCarrierMappingCreateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String productionStatus;
    private String processStatus;
    private BigDecimal quantity;
    private BigDecimal galQuantity;
    private Long mngKey;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private String mantiRequestState;
    private LocalDateTime mantiRequestTime;
    private LocalDateTime mantiReplyTime;
    private String rrnRequestState;
    private LocalDateTime rrnRequestTime;
    private LocalDateTime rrnReplyTime;
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

    public void loadCompleted(LoadCompletedCommand command){
        this.apply(command.getTransactionInfo());
        setMngKey(TsidUtils.nextId());
        setMantiRequestState(MantiRequestState.WAIT.getValue());
        // port에 도착한 시간 기록
        setMantiRequestTime(command.getTransactionInfo().eventTime());
        setMantiReplyTime(null);
    }
}