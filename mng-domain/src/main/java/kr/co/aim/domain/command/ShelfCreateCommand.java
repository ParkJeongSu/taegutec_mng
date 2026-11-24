package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ShelfCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String locationId;
    private final String equipmentName;
    private final Long zoneId;
    private final Long areaId;
    private final Integer capacity;
    private final String shelfState;
    private final String communicationState;
    private final String holdState;
    private final Integer length;
    private final Integer width;
    private final Integer height;
}
