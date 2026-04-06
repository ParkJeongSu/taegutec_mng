package kr.co.aim.domain.command;

import kr.co.aim.common.enums.EquipmentState;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class EquipmentStateReportCommand {
    private final TransactionInfo transactionInfo;
    private final EquipmentState equipmentState;
    private final String communicationState;
}
