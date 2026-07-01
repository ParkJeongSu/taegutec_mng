package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class CarrierDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String carrierDefName;
    private final String factoryName;
    private final String description;
    private final String carrierType;
    private final String carrierDetailType;
    private final Integer defaultCapacity;
    private final Integer useCountLimit;
    private final Integer useDurationLimit;
    private final Integer countLimitPerClean;
    private final Integer durationLimitPerClean;
    private final Integer cleanCountLimit;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
    private final String eventName;
    private final LocalDateTime eventTime;
    private final String eventUser;
    private final String eventComment;
}
