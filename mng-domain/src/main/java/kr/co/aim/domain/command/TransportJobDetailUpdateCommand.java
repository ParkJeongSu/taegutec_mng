package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class TransportJobDetailUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String transportJobDetailName;
    private final Long transportJobId;
    private final String transportJobDetailState;
    private final String carrierId;
    private final String sourceEquipmentName;
    private final String sourcePortName;
    private final String sourceZoneName;
    private final String sourceShelfName;
    private final String destinationEquipmentName;
    private final String destinationPortName;
    private final String destinationZoneName;
    private final String destinationShelfName;
    private final String currentEquipmentName;
    private final String currentPortName;
    private final String currentZoneName;
    private final String currentShelfName;
    private final Integer order;
    private final Integer jobPhase;
}
