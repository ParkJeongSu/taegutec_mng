package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(WcsRouteNodeId.class)
@Table(name = "ROUTE_NODE", catalog = "NEXBEWCS", schema = "dbo")
public class WcsRouteNodeEntity {

    @Id
    @Column(name = "factoryName", length = 30)
    private String factoryName;

    @Id
    @Column(name = "routeNodeId")
    private Long routeNodeId;

    @Column(name = "bayId", length = 30)
    private String bayId;

    @Column(name = "controllerType", length = 30)
    private String controllerType;

    @Column(name = "craneId", length = 30)
    private String craneId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "equipmentId", length = 30)
    private String equipmentId;

    @Column(name = "nodeEquipmentType", length = 30)
    private String nodeEquipmentType;

    @Column(name = "nodeId", length = 30)
    private String nodeId;

    @Column(name = "nodeName", length = 50)
    private String nodeName;

    @Column(name = "nodeSeq")
    private Integer nodeSeq;

    @Column(name = "rerouteType", length = 30)
    private String rerouteType;

    @Column(name = "routeNodeType", length = 30)
    private String routeNodeType;

    @Column(name = "unitId", length = 30)
    private String unitId;

    @Column(name = "useYn", length = 1)
    private String useYn;

    @Column(name = "lastEventComment", length = 255)
    private String lastEventComment;

    @Column(name = "lastEventName", length = 50)
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser", length = 30)
    private String lastEventUser;
}
