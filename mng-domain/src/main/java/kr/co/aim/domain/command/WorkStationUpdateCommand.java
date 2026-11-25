package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class WorkStationUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String workStationName;
    private final String equipmentName;
}
