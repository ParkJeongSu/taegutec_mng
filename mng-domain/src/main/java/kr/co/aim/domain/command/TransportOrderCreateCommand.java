package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class TransportOrderCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String transportOrderId;
    private final Long idocId;
    private final String description;
    private final String carrierName;
    private final String transportType;
    private final String transportStatus;
    private final String lastTransactionCode;
    private final String carrierType;
    private final Integer priority;
    private final Long galId; // cGalId only reply need
    private final String galWarehouse; // cGalWarehouse only reply need
    private final String locationId; // storage location Number 저장 위치 xxyyzz…
    private final String workStationId; //Inbound : 현재위치, Outbound : 타겟의 위치 Outbound 에서 목적지로 사용
    private final String sourceZoneName;
    private final String destinationZoneName;
    private final String errorText;
    private final String actualWeight;
    private final String requestedZoneName;
    private final String actualZoneName;
    private final String actualLocationId;
    private final String travelProfile;
    private final LocalDateTime createTime;
    private final LocalDateTime releaseTime;
    private final LocalDateTime completeTime;
    private final LocalDateTime retrievalTime;
    private final String createUser;
    private final String releaseUser;
    private final String completeUser;
}
