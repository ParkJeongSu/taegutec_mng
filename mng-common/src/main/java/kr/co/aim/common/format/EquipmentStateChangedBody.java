package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStateChangedBody {
    private String equipmentName;
    private String equipmentType;
    private String equipmentStateName;
}
