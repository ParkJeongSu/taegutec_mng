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
public class PalletBagBindingRequest {
    private Long id;
    private String orderId;
    private String lotName;
    private String itemName;
    private String carrierName;
    private BigDecimal quantity;
    private String eventUser;
    private String eventComment;

}
