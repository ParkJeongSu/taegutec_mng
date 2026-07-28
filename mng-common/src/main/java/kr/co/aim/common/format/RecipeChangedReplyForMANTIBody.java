package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeChangedReplyForMANTIBody {
    private String equipmentName;
    private String orderId;
    private String orderLineNumber;
    private RecipeBody recipe;
}
