package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class PortList {
    private String equipmentName;
    private String portName;
    private String portStateName;
    private String portType;
    private String portTransportMode;
    private String carrierName;
}