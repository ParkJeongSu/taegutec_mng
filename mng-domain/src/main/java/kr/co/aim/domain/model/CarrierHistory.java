package kr.co.aim.domain.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierHistory implements IBaseHistoryEntity {
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
