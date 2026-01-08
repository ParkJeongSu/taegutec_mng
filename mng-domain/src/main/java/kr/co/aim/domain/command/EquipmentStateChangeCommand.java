package kr.co.aim.domain.command;

import kr.co.aim.common.enums.EquipmentState;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Builder
public class EquipmentStateChangeCommand {
    private final TransactionInfo transactionInfo;
    private final EquipmentState equipmentState;
}
