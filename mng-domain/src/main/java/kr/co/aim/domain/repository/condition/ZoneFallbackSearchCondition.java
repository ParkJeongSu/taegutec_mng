package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneFallbackSearchCondition {
    private Long sourceZoneId;
    private Long targetZoneId;
    private Integer priority;
    private String eventName;
    private String eventUser;
}
