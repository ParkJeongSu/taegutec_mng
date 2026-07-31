package kr.co.aim.domain.model;

import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LotCarrierMappingHistory implements IBaseHistoryEntity {

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
}