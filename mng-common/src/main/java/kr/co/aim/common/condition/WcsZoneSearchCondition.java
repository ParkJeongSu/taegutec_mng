package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZoneSearchCondition {

    private String factoryName;
    private String zoneName;
    private String zoneType;
    private String loadType;
    private Boolean waitingAreaFlag;
    private String shelfSelectMode;
    private String zoneColor;
    private Boolean deepFirstFlag;
}
