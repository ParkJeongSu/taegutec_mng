package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class CarriersCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String carrierName;
    private final Long carrierDefId;
    private final String carrierState;
    private final String equipmentName;
    private final String portName;
    private final String zoneName;
    private final String shelfName;
    private final Integer capacity;
    private final String cleanState;
    private final String transportState;
    private final String reservedObjectId;
    private final String holdState;
    private final String reasonCode;
    private final String useState;
    private final Integer useCount;
    private final Integer useCountPerClean;
    private final Integer cleanCount;
    private final Integer lotQuantity;
    private final String capaState;
    private final LocalDateTime lastCleanTime;
    private final LocalDateTime createTime;
    private final String containerType;
}
