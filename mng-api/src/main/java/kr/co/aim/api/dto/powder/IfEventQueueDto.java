package kr.co.aim.api.dto.powder;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class IfEventQueueDto {

    private String productionOrderId;
    private String messageName;
    private String eventType;
    private String transactionCode;
    private String carrierName;
    private String idocId; // 기존 order idocId
    private String orderId;
    private String orderLineNumber; // insert 에선 현재로선 필요 없음
    private BigDecimal quantity;
    private BigDecimal missQuantity;
    private BigDecimal scrapQuantity;
    private String orderType; // h2orderm.corderty
    private String galKey;
    private String lotName;
    private String itemName;
    private String mngKey;
    private String resultStatus;
    private String errorReason;


}