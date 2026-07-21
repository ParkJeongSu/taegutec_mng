package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class TransportOrderAcceptCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String transportOrderId;
    private final String transportStatus;
}
