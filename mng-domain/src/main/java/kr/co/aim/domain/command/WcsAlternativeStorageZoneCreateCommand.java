package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneCreateCommand {

    private TransactionInfo transactionInfo;

    private String alternativeZoneName;
    private String factoryName;
    private Integer priority;
    private String sourceZoneName;
    private String description;
    private String useYn;
}
