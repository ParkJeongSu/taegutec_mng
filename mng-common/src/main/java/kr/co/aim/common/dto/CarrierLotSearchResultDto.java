package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CarrierLotSearchResultDto {

    // === CARRIER Fields ===
    private Long carrierId;
    private String carrierName;
    private String carrierDefName;
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
    private String carrierHoldState;
    private String carrierReasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private BigDecimal carrierQuantity;
    private BigDecimal galQuantity;
    private LocalDateTime lastCleanTime;
    private LocalDateTime carrierCreateTime;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String containerType;

    // === LOT_CARRIER_MAPPING Fields ===
    private Long mappingId;
    private String orderId;
    private String orderLineNumber;
    private Long productionOrderId;
    private String productionStatus;
    private String processStatus;
    private BigDecimal mappingQuantity;
    private BigDecimal mappingGalQuantity;
    private Long mngKey;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobEndTime;
    private String mantiRequestState;
    private LocalDateTime mantiRequestTime;
    private LocalDateTime mantiReplyTime;
    private String rrnRequestState;
    private LocalDateTime rrnRequestTime;
    private LocalDateTime rrnReplyTime;
    private String mappingHoldState;
    private String mappingReasonCode;

    // === LOT Fields ===
    private Long lotId;
    private String lotName;
    private String originalLotName;
    private String lotStatus;
    private String itemId;
    private BigDecimal totalQuantity;
    private String lotHoldState;
    private String lotReasonCode;
}