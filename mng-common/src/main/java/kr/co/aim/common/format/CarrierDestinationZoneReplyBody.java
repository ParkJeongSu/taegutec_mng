package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDestinationZoneReplyBody {
    private String carrierName;
    private String orderId;
    private String orderLineNumber;
    private String productionType;
    private String lotName;
    private String itemName;
    private String carrierState;
    private String containerType;

}
