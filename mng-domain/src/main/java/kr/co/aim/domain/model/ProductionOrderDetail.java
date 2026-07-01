package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.command.ProductionOrderDetailCreateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductionOrderDetail {

    private Long id;
    private Long productionOrderId;
    private String orderId;
    private String orderLineNumber;
    private Integer seq;
    private String carrierName;
    private String jobState;
    private BigDecimal allocatedQuantity;
    private BigDecimal actualQuantity;
    private LocalDateTime sendTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static ProductionOrderDetail create(ProductionOrderDetailCreateCommand command) {
        return ProductionOrderDetail.builder()
                .id(TsidUtils.nextId())
                .productionOrderId(command.getProductionOrderId())
                .orderId(command.getOrderId())
                .orderLineNumber(command.getOrderLineNumber())
                .seq(command.getSeq())
                .carrierName(command.getCarrierName())
                .jobState(command.getJobState())
                .allocatedQuantity(command.getAllocatedQuantity())
                .actualQuantity(command.getActualQuantity())
                .sendTime(command.getSendTime())
                .startTime(command.getStartTime())
                .completeTime(command.getCompleteTime())
                .createTime(command.getCreateTime())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
}