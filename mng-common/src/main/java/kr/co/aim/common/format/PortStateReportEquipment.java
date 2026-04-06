package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class PortStateReportEquipment {
    String equipmentName;
    List<PortList> portList;
}
