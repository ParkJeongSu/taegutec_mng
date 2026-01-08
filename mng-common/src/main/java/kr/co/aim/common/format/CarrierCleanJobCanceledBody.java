package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CarrierCleanJobCanceledBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String reasonCode;
    private String reasonCodeDescription;
}
