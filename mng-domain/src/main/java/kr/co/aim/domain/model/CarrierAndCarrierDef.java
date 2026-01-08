package kr.co.aim.domain.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CarrierAndCarrierDef {

    private Long id;
    private String carrierCode;
    private boolean reserved;
    private boolean error;
    private String carrierDefName;
    private String carrierType;
    private String carrierType2;
}
