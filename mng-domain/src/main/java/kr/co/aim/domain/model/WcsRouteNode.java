package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsRouteNodeCreateCommand;
import kr.co.aim.domain.command.WcsRouteNodeUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNode implements HasTransactionInfo {

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

    public static WcsRouteNode create(WcsRouteNodeCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsRouteNodeCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsRouteNode created";
        LocalDateTime eventTime = now;

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                eventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                eventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                eventComment = command.getTransactionInfo().eventComment().trim();
            }
            if (command.getTransactionInfo().eventTime() != null) {
                eventTime = command.getTransactionInfo().eventTime();
            }
        }

        return WcsRouteNode.builder()
                .factoryName(command.getFactoryName())
                .routeNodeId(command.getRouteNodeId())
                .bayId(command.getBayId())
                .controllerType(command.getControllerType())
                .craneId(command.getCraneId())
                .description(command.getDescription())
                .equipmentId(command.getEquipmentId())
                .nodeEquipmentType(command.getNodeEquipmentType())
                .nodeId(command.getNodeId())
                .nodeName(command.getNodeName())
                .nodeSeq(command.getNodeSeq() != null ? command.getNodeSeq() : 0)
                .rerouteType(command.getRerouteType())
                .routeNodeType(command.getRouteNodeType())
                .unitId(command.getUnitId())
                .useYn(command.getUseYn() != null ? command.getUseYn() : "Y")
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsRouteNode update(WcsRouteNodeUpdateCommand command) {
        if (command.getBayId() != null) {
            this.bayId = command.getBayId();
        }
        if (command.getControllerType() != null) {
            this.controllerType = command.getControllerType();
        }
        if (command.getCraneId() != null) {
            this.craneId = command.getCraneId();
        }
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getEquipmentId() != null) {
            this.equipmentId = command.getEquipmentId();
        }
        if (command.getNodeEquipmentType() != null) {
            this.nodeEquipmentType = command.getNodeEquipmentType();
        }
        if (command.getNodeId() != null) {
            this.nodeId = command.getNodeId();
        }
        if (command.getNodeName() != null) {
            this.nodeName = command.getNodeName();
        }
        if (command.getNodeSeq() != null) {
            this.nodeSeq = command.getNodeSeq();
        }
        if (command.getRerouteType() != null) {
            this.rerouteType = command.getRerouteType();
        }
        if (command.getRouteNodeType() != null) {
            this.routeNodeType = command.getRouteNodeType();
        }
        if (command.getUnitId() != null) {
            this.unitId = command.getUnitId();
        }
        if (command.getUseYn() != null) {
            this.useYn = command.getUseYn();
        }

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                this.lastEventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                this.lastEventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                this.lastEventComment = command.getTransactionInfo().eventComment().trim();
            }
            this.lastEventTime = (command.getTransactionInfo().eventTime() != null) ? command.getTransactionInfo().eventTime() : LocalDateTime.now();
        }

        return this;
    }

    @Override
    public void setEventName(String v) {
        this.lastEventName = v;
    }

    @Override
    public void setEventTime(LocalDateTime v) {
        this.lastEventTime = v;
    }

    @Override
    public void setEventUser(String v) {
        this.lastEventUser = v;
    }

    @Override
    public void setEventComment(String v) {
        this.lastEventComment = v;
    }
}
