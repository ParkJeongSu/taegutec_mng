package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class LoadRequestBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String portType;
    private String portTransportMode;
    private String transportJobName;
    private String actualWeight;
}
