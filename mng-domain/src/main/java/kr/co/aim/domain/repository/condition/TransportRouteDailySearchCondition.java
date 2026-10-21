package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransportRouteDailySearchCondition {
    private String statDate;
    private String sourceEquipmentName;
    private String destinationEquipmentName;
}