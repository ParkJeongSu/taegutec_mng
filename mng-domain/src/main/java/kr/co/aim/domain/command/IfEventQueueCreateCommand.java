package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class IfEventQueueCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String eventType;
    private final String payload;
    private final String ifStatus;
    private final String carrierName;
    private final String idocId;
    private final String orderId;
    private final String orderLineNumber;
    private final Integer retryCNT;
    private final String errMSG;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;
}
