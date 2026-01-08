package kr.co.aim.domain.command;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class PortsCreateCommand {
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
