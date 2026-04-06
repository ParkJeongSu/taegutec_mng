package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class TransportJobCancelCompletedBody {
    private String transportJobName;
    private String transportType;
    private String carrierName;
    private String currentEquipmentName;
    private String currentPortName;
    private String currentZoneName;
    private String currentPositionTypeName;
    private String currentPositionName;
    private String orderId;
    private String requestSource;
    private String travelProfile;
    private String actualWeight;
    private String carrierType;
    private List<TransportJobCancelCompletedReasonBody> reasons;
}