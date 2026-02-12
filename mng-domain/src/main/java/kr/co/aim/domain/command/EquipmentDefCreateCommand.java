package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class EquipmentDefCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String equipmentDefName;
    private final String description;
    private final String equipmentType;
    private final Long equipmentGroupId;
    private final String detailEquipmentType;
    private final String stateModel;
    private final String vendorId;
    private final String modelId;
    private final Integer processCapacity;
    private final Integer loadingCapacity;
    private final String containerType;
}
