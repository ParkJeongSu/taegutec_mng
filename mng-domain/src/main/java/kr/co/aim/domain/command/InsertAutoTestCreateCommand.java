package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class InsertAutoTestCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String wareHouseName;
    private final String workStationId;
    private final String carrierName;
    private final String zoneName;
    private final String inboundPortName;
    private final String travelProfile;
}
