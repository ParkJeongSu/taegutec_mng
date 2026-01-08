package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class WhereNextBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String portType;
    private String portTransportMode;
}
