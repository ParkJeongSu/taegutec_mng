package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDataRemovedBody {
    private String carrierName;
    private String equipmentName;
}
