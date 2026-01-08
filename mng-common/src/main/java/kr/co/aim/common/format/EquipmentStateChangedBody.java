package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class EquipmentStateChangedBody {
    private String equipmentName;
    private String equipmentType;
    private String equipmentStateName;
}
