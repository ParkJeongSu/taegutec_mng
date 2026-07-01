package kr.co.aim.domain.model;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierHistory implements IBaseHistoryEntity {
    private Long id;
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
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private BigDecimal quantity;
    private BigDecimal galQuantity;
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
