package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MaterialSearchCondition {
    private String materialId;
    private String description;
    private Long materialClassId;
    private Long materialGroupId;
    private String carrierName;
    private BigDecimal quantity;
    private String eventName;
    private String eventUser;
}
