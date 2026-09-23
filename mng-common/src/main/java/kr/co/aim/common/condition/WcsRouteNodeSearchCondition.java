package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNodeSearchCondition {

    private String factoryName;
    private Long routeNodeId;
    private String nodeId;
    private String nodeName;
    private String equipmentId;
    private String unitId;
    private String bayId;
    private String craneId;
    private String routeNodeType;
    private String nodeEquipmentType;
    private String controllerType;
    private String rerouteType;
    private String useYn;
    private Integer nodeSeq;
}
