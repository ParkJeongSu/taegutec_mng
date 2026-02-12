package kr.co.aim.common.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDeassignFromCarrier {

    private String carrierName;
    private String equipmentName;
}