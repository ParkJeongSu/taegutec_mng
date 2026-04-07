package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDataInstalledBody {
    private String carrierName;
    private String carrierType;
    private String currentEquipmentName;
    private String currentPositionType;
    private String currentPositionName;
    private String currentZoneName;
}
