package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class EquipmentStateReportBodyList {
    private List<EquipmentStateReportBody> equipmentList;
}
