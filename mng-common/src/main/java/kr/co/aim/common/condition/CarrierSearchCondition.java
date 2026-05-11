package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class CarrierSearchCondition {
    private Long id;
    private String carrierName;
    private Long carrierDefId;
    private String carrierState;
    private String equipmentName;
    private String portName;
    private String zoneName;
    private String positionTypeName;
    private String positionName;
    private Integer capacity;
    private String cleanState;
    private String transportState;
    private String transportJobId;
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private String lotName;
    private String lotStatus;
    private String orderId;
    private String orderLineNumber;
    private String itemId;
    private Integer quantity;
    private String lastIdocId;
    private String interfaceStatus;
    private LocalDateTime interfaceRequestTime;
    private LocalDateTime interfaceReplyTime;
    private String equipmentFlag;
    private LocalDateTime jobEndTime;
    private LocalDateTime lastCleanTime;
    private LocalDateTime createTime;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String containerType;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

}