package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class LocationChangedCommand {
    private final TransactionInfo transactionInfo;
    private final String equipmentName;
    private final String portName;
    private final String zoneName;
    private final String positionType;
    private final String positionName;

}
