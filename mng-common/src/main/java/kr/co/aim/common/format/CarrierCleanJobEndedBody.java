package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierCleanJobEndedBody {
    private String equipmentName;
    private String portName;
    private String carrierName;
}
