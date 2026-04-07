package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortList {
    private String equipmentName;
    private String portName;
    private String portStateName;
    private String portType;
    private String portTransportMode;
    private String carrierName;
}