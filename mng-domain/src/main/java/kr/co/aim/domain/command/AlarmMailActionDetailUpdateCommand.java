package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmMailActionDetailUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String subject;
    private final String contents;

}
