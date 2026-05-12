package kr.co.aim.domain.model;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentDef {
    private String equipmentName;
    private String factoryName;
    private String description;
    private String equipmentType;
    private String equipmentGroupName;
    private String detailEquipmentType;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private String containerType;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;
    private String lastEventComment;

}
