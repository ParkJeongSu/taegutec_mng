package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class TransportJobCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String transportJobName;
    private final String carrierName;
    private final String transportType;
    private final String transportJobState;
    private final String carrierType;
    private final String drivingProfile;
    private final String sourceEquipmentName;
    private final String sourcePortName;
    private final String sourceZoneName;
    private final String sourcePositionTypeName;
    private final String sourcePositionName;
    private final String destinationEquipmentName;
    private final String destinationPortName;
    private final String destinationZoneName;
    private final String destinationPositionTypeName;
    private final String destinationPositionName;
    private final Integer priority;
    private final String errorCode;
    private final String errorText;
    private final String requestType;
    private final LocalDateTime createTime;
    private final LocalDateTime departedTime;
    private final LocalDateTime arrivedTime;
    private final String reasonCode;
    private final String orderId;
}
