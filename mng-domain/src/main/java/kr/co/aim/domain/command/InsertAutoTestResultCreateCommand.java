package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class InsertAutoTestResultCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long autoTestId;
    private final String state;
    private final String result;
}
