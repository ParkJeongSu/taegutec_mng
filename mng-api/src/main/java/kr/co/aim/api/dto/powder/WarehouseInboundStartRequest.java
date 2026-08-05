package kr.co.aim.api.dto.powder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WarehouseInboundStartRequest {
    private Long id;
    private String carrierName;
    private String sourceEquipmentName;
    private String sourceZoneName;
    private String sourcePositionType;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationZoneName;
    private String destinationPositionType;
    private String destinationPositionName;
    private String orderId;
    private String lotName;
    private String itemName;
    private String requestSource;
    private String carrierType;
    private String eventUser;
    private String eventComment;
}
