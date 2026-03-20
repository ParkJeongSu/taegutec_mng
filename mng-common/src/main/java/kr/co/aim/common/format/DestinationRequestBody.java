package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DestinationRequestBody {
    private String transportJobName; // 이 부분 좀 고민해보자
    private String carrierName;
    private String currentEquipmentName;
    private String currentPositionType;
    private String currentPositionName;
    private String currentZoneName;
}
