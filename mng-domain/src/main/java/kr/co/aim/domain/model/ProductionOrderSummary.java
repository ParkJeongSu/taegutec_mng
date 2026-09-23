package kr.co.aim.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductionOrderSummary {
    private Long id;
    private String orderId;
    private String lotName;
    private String description;
    private String itemName;
    private String productionOrderType;
    private BigDecimal planQuantity;
    private BigDecimal releasedQuantity;
    private BigDecimal startedQuantity;
    private BigDecimal endedQuantity;
    private BigDecimal scrappedQuantity;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
    private LocalDateTime dueDate;

}
