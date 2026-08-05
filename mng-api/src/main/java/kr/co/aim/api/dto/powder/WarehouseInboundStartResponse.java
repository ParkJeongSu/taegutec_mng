package kr.co.aim.api.dto.powder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WarehouseInboundStartResponse {
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

    public static WarehouseInboundStartResponse from (WarehouseInboundStartRequest warehouseInboundStartRequest) {
        return WarehouseInboundStartResponse
                .builder()
                .carrierName(warehouseInboundStartRequest.getCarrierName())
                .sourceEquipmentName(warehouseInboundStartRequest.getSourceEquipmentName())
                .sourceZoneName(warehouseInboundStartRequest.getSourceZoneName())
                .sourcePositionType(warehouseInboundStartRequest.getSourcePositionType())
                .sourcePositionName(warehouseInboundStartRequest.getSourcePositionName())
                .destinationEquipmentName(warehouseInboundStartRequest.getDestinationEquipmentName())
                .destinationZoneName(warehouseInboundStartRequest.getDestinationZoneName())
                .destinationPositionType(warehouseInboundStartRequest.getDestinationPositionType())
                .destinationPositionName(warehouseInboundStartRequest.getDestinationPositionName())
                .orderId(warehouseInboundStartRequest.getOrderId())
                .lotName(warehouseInboundStartRequest.getLotName())
                .itemName(warehouseInboundStartRequest.getItemName())
                .requestSource(warehouseInboundStartRequest.getRequestSource())
                .carrierType(warehouseInboundStartRequest.getCarrierType())
                .eventUser(warehouseInboundStartRequest.getEventUser())
                .eventComment(warehouseInboundStartRequest.getEventComment())
                .build();
    }
}
