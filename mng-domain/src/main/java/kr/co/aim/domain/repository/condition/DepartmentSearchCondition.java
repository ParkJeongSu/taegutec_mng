package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentSearchCondition {
    private String factoryName;
    private String departmentName;
    private String useState;
}
