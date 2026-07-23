package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LotCarrierMappingSearchCondition {
    private String lotName;
    private String carrierName;
    private String orderId;
    private String orderLineNumber;
    private Long mngKey;
    private String mantiRequestState;
}