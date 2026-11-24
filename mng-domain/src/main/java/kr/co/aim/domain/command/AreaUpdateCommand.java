package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class AreaUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String areaName;
    private final String description;
}
