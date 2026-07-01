package kr.co.aim.common.format;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierInfoDownloadSendBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String productionTaskId;
    private String lotName;
    private String itemName;
    private String orderId;
    private String orderLineNumber;
    private String quantity;
    private String totalQuantity;
    private String mngKey;
    private String lastCarrierFlag;
    private RecipeBody recipe;
}
