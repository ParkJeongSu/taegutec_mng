package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessJobEndedBody {
    private String equipmentName;
    private String portName;
    private String portType;
    private String portTransportMode;
    private String lotName;
    private String weight;
    private String carrierName;
    private String carrierState;
    private String carrierType;
}
