package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class ProductionOrderHistorySearchCondition {
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
    private LocalDateTime fromEventTime;
    private LocalDateTime toEventTime;


}