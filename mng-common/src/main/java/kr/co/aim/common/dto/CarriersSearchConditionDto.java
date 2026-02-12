package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class CarriersSearchConditionDto {
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
    private String containerType;
}