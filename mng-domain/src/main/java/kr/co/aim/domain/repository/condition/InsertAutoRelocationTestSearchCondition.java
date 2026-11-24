package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertAutoRelocationTestSearchCondition {
    private String wareHouseName1;
    private String wareHouseName2;
    private String zoneName1;
    private String zoneName2;
    private String carrierName;
    private String state;
    private String eventName;
    private String eventUser;
}
