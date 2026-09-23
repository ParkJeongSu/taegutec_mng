package kr.co.aim.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZoneUpdateRequestDto {

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

    private String eventName;
    private String eventUser;
    private String eventComment;
}
