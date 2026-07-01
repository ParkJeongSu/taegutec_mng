package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeParameterListBody {
    private String parameterName;
    private String parameterValue;
    private String minValue;
    private String maxValue;
}
