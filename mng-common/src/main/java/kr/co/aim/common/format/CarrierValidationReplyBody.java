package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierValidationReplyBody {
    private String equipmentName;
    private String portName;
    private String portTransportMode;
    private String carrierName;
}
