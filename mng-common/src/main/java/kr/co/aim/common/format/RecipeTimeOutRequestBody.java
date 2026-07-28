package kr.co.aim.common.format;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeTimeOutRequestBody {
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
}
