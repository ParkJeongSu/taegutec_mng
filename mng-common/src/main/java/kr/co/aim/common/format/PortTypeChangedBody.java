package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class PortTypeChangedBody {
    private String equipmentName;
    private String portName;
    private String portType;
    private String portTransportMode;
}
