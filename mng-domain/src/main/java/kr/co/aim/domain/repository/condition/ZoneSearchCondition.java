package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneSearchCondition {
    private String zoneName;
    private String zoneType;
    private String description;
    private String zoneColor;
    private String eventName;
    private String eventUser;
}
