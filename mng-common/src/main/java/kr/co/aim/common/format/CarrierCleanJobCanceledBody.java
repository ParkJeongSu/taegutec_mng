package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierCleanJobCanceledBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
    private String reasonCode;
    private String reasonCodeDescription;
}
