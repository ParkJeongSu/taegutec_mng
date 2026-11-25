package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialDefSearchCondition {
    private String materialDefName;
    private String description;
    private Long materialClassId;
    private Long materialGroupId;
    private String unit;
    private String isSerialTracked;
    private String eventName;
    private String eventUser;
}
