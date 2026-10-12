package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertAutoTestSearchCondition {
    private String warehouseName;
    private String workStationId;
    private String carrierName;
    private String zoneName;
    private String inboundPortName;
    private String travelProfile;
    private String eventName;
    private String eventUser;
}
