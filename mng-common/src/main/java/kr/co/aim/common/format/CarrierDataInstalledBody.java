package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CarrierDataInstalledBody {
    private String carrierName;
    private String carrierType;
    private String currentEquipmentName;
    private String currentPositionType;
    private String currentPositionName;
    private String currentZoneName;
}
