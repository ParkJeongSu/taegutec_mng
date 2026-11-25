package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkStationSearchCondition {
    private String workStationName;
    private String equipmentName;
    private String eventName;
    private String eventUser;
}
