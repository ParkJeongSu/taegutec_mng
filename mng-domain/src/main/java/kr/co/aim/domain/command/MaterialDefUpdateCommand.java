package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MaterialDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String materialDefName;
    private final String description;
    private final Long materialClassId;
    private final Long materialGroupId;
    private final String unit;
    private final String isSerialTracked;
}
