package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PortDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String equipmentName;
    private final String portName;
    private final String factoryName;
    private final Integer portNumber;
    private final String description;
    private final String transportMode;
    private final String portType;
    private final String detailPortType;
    private final String portUseType;
    private final String portRoleType;
    private final String workCenterName;
    private final String locationId;
    private final String connectedEquipmentName;
    private final String connectedPortName;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
}