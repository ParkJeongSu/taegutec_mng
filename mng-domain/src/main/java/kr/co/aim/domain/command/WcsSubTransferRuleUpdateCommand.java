package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleUpdateCommand {

    private TransactionInfo transactionInfo;

    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;
}
