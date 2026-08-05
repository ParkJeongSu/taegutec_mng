package kr.co.aim.api.dto.powder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RawMaterialReceivingStartResponse {
    private Long id;
    private String orderId;
    private String lotName;
    private String itemName;
    private String eventUser;
    private String eventComment;

    public static RawMaterialReceivingStartResponse from(RawMaterialReceivingStart rawMaterialReceivingStart) {
        return RawMaterialReceivingStartResponse
                .builder()
                .id(rawMaterialReceivingStart.getId())
                .orderId(rawMaterialReceivingStart.getOrderId())
                .lotName(rawMaterialReceivingStart.getLotName())
                .itemName(rawMaterialReceivingStart.getItemName())
                .eventUser(rawMaterialReceivingStart.getEventUser())
                .eventComment(rawMaterialReceivingStart.getEventComment())
                .build();
    }
}
