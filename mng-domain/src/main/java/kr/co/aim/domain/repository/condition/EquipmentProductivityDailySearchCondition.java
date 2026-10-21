package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentProductivityDailySearchCondition {
    private String statDate;
    private String equipmentName;
}