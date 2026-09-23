package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZoneCreateCommand {

    private TransactionInfo transactionInfo;

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
}
