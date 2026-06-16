package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportJobReplyByWMSBody {
    private String transportJobName;
    private String carrierName;
    private String sourceEquipmentName;
    private String sourceZoneName;
    private String sourcePositionType;
    private String sourcePositionName;
    private String destinationEquipmentName;
    private String destinationZoneName;
    private String destinationPositionType;
    private String destinationPositionName;
    private String priority;
    private String carrierType;
    private String orderId;
    private String orderLineNumber;


}
