package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNodeUpdateRequestDto {

    private String bayId;
    private String controllerType;
    private String craneId;
    private String description;
    private String equipmentId;
    private String nodeEquipmentType;
    private String nodeId;
    private String nodeName;
    private Integer nodeSeq;
    private String rerouteType;
    private String routeNodeType;
    private String unitId;
    private String useYn;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
