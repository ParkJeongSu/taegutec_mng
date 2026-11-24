package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MaterialClassDefCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String materialClassDefName;
    private final String materialClassType;
    private final Long parentClassId;
    private final Integer classLevel;
    private final String description;
}
