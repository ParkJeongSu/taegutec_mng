package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessJobStartedBody {
    private String equipmentName;
    private String portName;
    private String lotName;
    private String carrierName;
    private String recipeName;
}
