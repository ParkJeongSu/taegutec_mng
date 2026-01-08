package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class EquipmentStateReportBody {
    private String equipmentName;
    private String equipmentType;
    private String equipmentStateName;
    private String communicationState;
}
