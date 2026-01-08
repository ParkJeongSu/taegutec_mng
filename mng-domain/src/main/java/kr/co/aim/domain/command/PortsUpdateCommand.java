package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class PortsUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String equipmentName;
    private final String portName;
    private final Long portDefId;
    private final String description;
    private final String connectedStocker;
    private final String transportMode;
    private final String portState;
    private final String resourceState;
    private final String transportState;
    private final String carrierName;
}
