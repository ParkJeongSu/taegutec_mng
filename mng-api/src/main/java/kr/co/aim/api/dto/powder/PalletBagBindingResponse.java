package kr.co.aim.api.dto.powder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PalletBagBindingResponse {
    private Long id;
    private String orderId;
    private String lotName;
    private String itemName;
    private String carrierName;
    private BigDecimal quantity;
    private String eventUser;
    private String eventComment;

    public static PalletBagBindingResponse from(PalletBagBindingRequest request){
        return PalletBagBindingResponse
                .builder()
                .id(request.getId())
                .orderId(request.getOrderId())
                .lotName(request.getLotName())
                .itemName(request.getItemName())
                .carrierName(request.getCarrierName())
                .quantity(request.getQuantity())
                .eventUser(request.getEventUser())
                .eventComment(request.getEventComment())
                .build();
    }
}
