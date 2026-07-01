package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class CarrierCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String carrierName;
    private final String carrierDefName;
    private final String carrierState;
    private final String equipmentName;
    private final String portName;
    private final String zoneName;
    private final String positionTypeName;
    private final String positionName;
    private final Integer capacity;
    private final String cleanState;
    private final String transportState;
    private final String transportJobId;
    private final String holdState;
    private final String reasonCode;
    private final String useState;
    private final Integer useCount;
    private final Integer useCountPerClean;
    private final Integer cleanCount;
    private final BigDecimal quantity;
    private final BigDecimal galQuantity;
    private final LocalDateTime lastCleanTime;
    private final LocalDateTime createTime;
    private final LocalDateTime inboundTime;
    private final LocalDateTime outboundTime;
    private final String containerType;
    private final String eventName;
    private final LocalDateTime eventTime;
    private final String eventUser;
    private final String eventComment;
}
