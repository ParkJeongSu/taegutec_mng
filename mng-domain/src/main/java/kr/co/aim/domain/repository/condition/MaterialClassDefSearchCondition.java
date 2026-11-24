package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialClassDefSearchCondition {
    private String materialClassDefName;
    private String materialClassType;
    private Long parentClassId;
    private Integer classLevel;
    private String description;
    private String eventName;
    private String eventUser;
}
