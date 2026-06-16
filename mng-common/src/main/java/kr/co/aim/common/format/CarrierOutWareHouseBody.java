package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierOutWareHouseBody {
    private String equipmentName;
    private String zoneName;
    private String shelfName;
    private String carrierName;
    private String carrierType;
    private String carrierState;
    private String quantity;
    private String itemName;
    private String orderId;
    private String orderLineNumber;
}
