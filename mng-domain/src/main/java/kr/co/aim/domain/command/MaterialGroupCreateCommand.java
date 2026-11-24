package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MaterialGroupCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String materialGroupDefName;
    private final String description;
}
