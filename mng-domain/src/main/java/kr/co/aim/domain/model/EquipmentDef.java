package kr.co.aim.domain.model;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentDef {

    private Long id;
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
    private String plcType;
    private String routeKey;
    private String serverName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private Integer localNo;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

}
