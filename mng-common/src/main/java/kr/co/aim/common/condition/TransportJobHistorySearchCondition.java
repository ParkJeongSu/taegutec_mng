package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class TransportJobHistorySearchCondition {
    private String transportJobName;
    private String carrierName;
    private String transportType; // I O R
    private String transportJobState;
    private String carrierType;
    private String travelProfile;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourcePositionTypeName;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationPositionTypeName;
    private String destinationPositionName;
    private Integer priority;
    private String errorCode;
    private String errorText;
    private String requestSource;
    private LocalDateTime createTime;
    private LocalDateTime departedTime;
    private LocalDateTime arrivedTime;
    private String reasonCode;
    private LocalDateTime fromEventTime;
    private LocalDateTime toEventTime;
    private String orderId;
}