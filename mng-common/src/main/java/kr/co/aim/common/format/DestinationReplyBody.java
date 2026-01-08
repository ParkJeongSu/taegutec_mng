package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DestinationReplyBody {
    private String transportJobName; // 이 부분 좀 고민해보자
    private String carrierName;
    private String carrierType;
    private String destinationEquipmentName;
    private String destinationPositionType;
    private String destinationPositionName;
    private String destinationZoneName;
    private String priority;
}
