package kr.co.aim.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class CarriersResponseDto {
    private Long id;
    private String carrierName;
    private Long carrierDefId;
    private String carrierDefName;
    private String carrierState;
    private String equipmentName;
    private String portName;
    private String zoneName;
    private String shelfName;
    private Integer capacity;
    private String cleanState;
    private String transportState;
    private String reservedObjectId;
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private Integer lotQuantity;
    private String capaState;
    private LocalDateTime lastCleanTime;
    private LocalDateTime createTime;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String containerType;

    @QueryProjection
    public CarriersResponseDto(
            Long id,
            String carrierName,
            Long carrierDefId,
            String carrierDefName,
            String carrierState,
            String equipmentName,
            String portName,
            String zoneName,
            String shelfName,
            Integer capacity,
            String cleanState,
            String transportState,
            String reservedObjectId,
            String holdState,
            String reasonCode,
            String useState,
            Integer useCount,
            Integer useCountPerClean,
            Integer cleanCount,
            Integer lotQuantity,
            String capaState,
            LocalDateTime lastCleanTime,
            LocalDateTime createTime,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment,
            String containerType
    )
    {
        this.id = id;
        this.carrierName = carrierName;
        this.carrierDefId = carrierDefId;
        this.carrierDefName = carrierDefName;
        this.carrierState = carrierState;
        this.equipmentName = equipmentName;
        this.portName = portName;
        this.zoneName = zoneName;
        this.shelfName = shelfName;
        this.capacity = capacity;
        this.cleanState = cleanState;
        this.transportState = transportState;
        this.reservedObjectId = reservedObjectId;
        this.holdState = holdState;
        this.reasonCode = reasonCode;
        this.useState = useState;
        this.useCount = useCount;
        this.useCountPerClean = useCountPerClean;
        this.cleanCount = cleanCount;
        this.lotQuantity = lotQuantity;
        this.capaState = capaState;
        this.lastCleanTime = lastCleanTime;
        this.createTime = createTime;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
        this.containerType = containerType;
    }
}