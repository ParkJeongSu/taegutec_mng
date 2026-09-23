package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleCreateCommand {

    private TransactionInfo transactionInfo;

    private String equipmentName;
    private String factoryName;
    private String moduleName;
    private Long routeLinkId;
    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;
}
