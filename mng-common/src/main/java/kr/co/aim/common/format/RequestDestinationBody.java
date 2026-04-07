package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDestinationBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String portType;
    private String portTransportMode;
}
