package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
