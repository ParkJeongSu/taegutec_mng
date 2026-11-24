package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelfSearchCondition {
    private String locationId;
    private String equipmentName;
    private Long zoneId;
    private Long areaId;
    private Integer capacity;
    private String shelfState;
    private String communicationState;
    private String holdState;
    private Integer length;
    private Integer width;
    private Integer height;
    private String eventName;
    private String eventUser;
}
