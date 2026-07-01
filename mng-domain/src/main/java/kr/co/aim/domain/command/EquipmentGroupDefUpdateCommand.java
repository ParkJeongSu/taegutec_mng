package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentGroupDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String description;
    private final String checkOutState;
    private final java.time.LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
}