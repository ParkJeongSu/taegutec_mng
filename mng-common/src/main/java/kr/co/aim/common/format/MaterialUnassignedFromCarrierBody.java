package kr.co.aim.common.format;

import lombok.*;

import java.math.BigDecimal;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialUnassignedFromCarrierBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String productionTaskId;
    private String orderId;
    private String orderLineNumber;
    private BigDecimal quantity;
    private String carrierStatus;
    private String mngKey;

}
