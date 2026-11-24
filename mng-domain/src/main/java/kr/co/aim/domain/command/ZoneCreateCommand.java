package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ZoneCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String zoneName;
    private final String zoneType;
    private final String description;
    private final String zoneColor;
}
