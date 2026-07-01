package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeReplyBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String orderId;
    private String orderLineNumber;
    private String transactionId;
    RecipeBody recipe;
}
