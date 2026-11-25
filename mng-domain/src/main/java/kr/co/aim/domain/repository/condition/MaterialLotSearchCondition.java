package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MaterialLotSearchCondition {
    private String materialLotId;
    private Long materialDefId;
    private Long parentMaterialLotId;
    private String carrierName;
    private BigDecimal quantity;
    private String state;
    private String eventName;
    private String eventUser;
}
