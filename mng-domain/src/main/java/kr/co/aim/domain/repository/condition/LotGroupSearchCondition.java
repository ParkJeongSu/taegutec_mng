package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class LotGroupSearchCondition {
    private String lotGroupName;
    private String originalLotGroupName;
    private String lotGroupStatus;
    private String itemId;
    private BigDecimal totalQuantity;
    private String holdState;
    private String reasonCode;
}