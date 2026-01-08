package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TransportJobCompletedBody {
    private String sourceEquipmentName;
    private String destinationEquipmentName;
    private String carrierName;
}