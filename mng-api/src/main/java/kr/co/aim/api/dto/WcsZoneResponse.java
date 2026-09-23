package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsZone;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZoneResponse {

    private String factoryName;
    private String zoneName;

    private Boolean deepFirstFlag;
    private Integer frontRowInterval;
    private String loadType;
    private BigDecimal maxCapacityPercent;
    private Integer zoneCapacity;
    private String zoneColor;
    private Integer zoneSize;
    private String zoneType;
    private String shelfSelectMode;
    private BigDecimal useCapacityPercent;
    private Boolean waitingAreaFlag;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsZoneResponse fromDomain(WcsZone domain) {
        if (domain == null) {
            return null;
        }

        return WcsZoneResponse.builder()
                .factoryName(domain.getFactoryName())
                .zoneName(domain.getZoneName())
                .deepFirstFlag(domain.getDeepFirstFlag())
                .frontRowInterval(domain.getFrontRowInterval())
                .loadType(domain.getLoadType())
                .maxCapacityPercent(domain.getMaxCapacityPercent())
                .zoneCapacity(domain.getZoneCapacity())
                .zoneColor(domain.getZoneColor())
                .zoneSize(domain.getZoneSize())
                .zoneType(domain.getZoneType())
                .shelfSelectMode(domain.getShelfSelectMode())
                .useCapacityPercent(domain.getUseCapacityPercent())
                .waitingAreaFlag(domain.getWaitingAreaFlag())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
