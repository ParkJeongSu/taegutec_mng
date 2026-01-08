package kr.co.aim.domain.command;

import kr.co.aim.common.enums.PortState;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class PortStateChangedCommand {
    private final TransactionInfo transactionInfo;
    private final PortState portState;

}
