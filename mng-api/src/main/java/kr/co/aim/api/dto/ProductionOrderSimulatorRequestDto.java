package kr.co.aim.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
@AllArgsConstructor
public class ProductionOrderSimulatorRequestDto {
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
    private String productionOrderType;
    private String productionOrderState;
    private String reportState;
    private String holdState;
    private String reasonCode;
    private String equipmentName;
    private BigDecimal actualQuantity;
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
}