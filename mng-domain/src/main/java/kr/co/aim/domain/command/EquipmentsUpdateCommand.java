package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class EquipmentsUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String equipmentName;
    private final Long equipmentDefId;
    private final Long parentEquipmentId;
    private final String equipmentLevel;
    private final String equipmentState;
    private final String communicationState;
    private final Integer processCount;
    private final String recipeName;
    private final String defaultStockerId;
    private final String defaultZoneId;
    private final String holdState;
    private final String reasonCode;
    private final String resourceState;
    private final String operationMode;
    private final String messageServiceAddress;
}
