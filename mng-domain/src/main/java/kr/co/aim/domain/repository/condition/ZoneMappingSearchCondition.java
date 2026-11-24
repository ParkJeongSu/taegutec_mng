package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneMappingSearchCondition {
    private Long zoneId;
    private String orderLineNumber;
    private String eventName;
    private String eventUser;
}
