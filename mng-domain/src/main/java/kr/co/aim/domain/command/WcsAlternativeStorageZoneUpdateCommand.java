package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneUpdateCommand {

    private TransactionInfo transactionInfo;

    private String description;
    private String useYn;
}
