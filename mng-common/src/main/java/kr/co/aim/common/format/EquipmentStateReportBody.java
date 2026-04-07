package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStateReportBody {
    private String equipmentName;
    private String equipmentType;
    private String equipmentStateName;
    private String communicationState;
}
