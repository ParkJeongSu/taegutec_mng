package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class LotSearchCondition {
    private String lotName;
    private String originalLotName;
    private String lotStatus;
    private String itemId;
    private BigDecimal totalQuantity;
    private String holdState;
    private String reasonCode;
}