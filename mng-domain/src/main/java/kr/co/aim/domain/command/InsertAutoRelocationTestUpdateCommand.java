package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class InsertAutoRelocationTestUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String wareHouseName1;
    private final String wareHouseName2;
    private final String zoneName1;
    private final String zoneName2;
    private final String carrierName;
    private final String state;
}
