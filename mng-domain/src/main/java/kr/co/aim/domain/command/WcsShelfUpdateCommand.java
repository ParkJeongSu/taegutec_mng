package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelfUpdateCommand {

    private TransactionInfo transactionInfo;

    private Integer abnormalStageNumber;
    private Integer bin;
    private String carrierName;
    private Integer col;
    private String lastTransferCmdName;
    private Integer numberOfUses;
    private Integer row;
    private String shelfEnableMode;
    private String shelfStatus;
    private String shelfTransferStatus;
    private String shelfType;
    private Integer stage;
    private String zoneName;
}
