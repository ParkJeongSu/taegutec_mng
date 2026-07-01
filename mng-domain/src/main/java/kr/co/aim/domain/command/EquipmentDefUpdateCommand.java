package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class EquipmentDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final String description;
    private final String equipmentType;
    private final String equipmentGroupName;
    private final String detailEquipmentType;
    private final String vendorId;
    private final String modelId;
    private final Integer processCapacity;
    private final String containerType;
    private final String plcType;
    private final String routeKey;
    private final String serverName;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
    private final Integer localNo;
}