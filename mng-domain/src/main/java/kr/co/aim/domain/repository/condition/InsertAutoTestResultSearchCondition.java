package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertAutoTestResultSearchCondition {
    private Long autoTestId;
    private String state;
    private String result;
    private String eventName;
    private String eventUser;
}
