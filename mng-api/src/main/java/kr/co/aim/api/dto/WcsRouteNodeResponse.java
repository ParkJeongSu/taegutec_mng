package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsRouteNode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNodeResponse {

    private String factoryName;
    private Long routeNodeId;
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

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsRouteNodeResponse fromDomain(WcsRouteNode domain) {
        if (domain == null) {
            return null;
        }

        return WcsRouteNodeResponse.builder()
                .factoryName(domain.getFactoryName())
                .routeNodeId(domain.getRouteNodeId())
                .bayId(domain.getBayId())
                .controllerType(domain.getControllerType())
                .craneId(domain.getCraneId())
                .description(domain.getDescription())
                .equipmentId(domain.getEquipmentId())
                .nodeEquipmentType(domain.getNodeEquipmentType())
                .nodeId(domain.getNodeId())
                .nodeName(domain.getNodeName())
                .nodeSeq(domain.getNodeSeq())
                .rerouteType(domain.getRerouteType())
                .routeNodeType(domain.getRouteNodeType())
                .unitId(domain.getUnitId())
                .useYn(domain.getUseYn())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
