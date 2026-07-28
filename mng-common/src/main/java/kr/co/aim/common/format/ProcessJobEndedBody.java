package kr.co.aim.common.format;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessJobEndedBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String productionTaskId;
    private String recipeName;
    private String orderId;
    private String orderLineNumber;
    private BigDecimal quantity;
    private String lotName;
    private String itemName;
    private String productionTaskEnd;
    private List<MngKeyName> mngKeyList;
}
