package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "PORT_DEF", catalog = "NEXBEWCS", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class PortDefEntity {

    @EmbeddedId
    private PortDefId id; // 복합키 클래스를 ID로 사용

    @Column(name = "description")
    private String description;

    @Column(name = "portType")
    private String portType;

    @Column(name = "detailPortType")
    private String detailPortType;

    @Column(name = "portUseType")
    private String portUseType;

    @Column(name = "portRoleType")
    private String portRoleType;

    @Column(name = "workCenterName")
    private String workCenterName;

    @Column(name = "locationId")
    private String locationId;

    @Column(name = "connectedEquipmentName")
    private String connectedEquipmentName;

    @Column(name = "connectedPortName")
    private String connectedPortName;

    @Column(name = "checkoutState")
    private String checkOutState;

    @Column(name = "checkoutTime")
    private LocalDateTime checkOutTime;

    @Column(name = "checkoutUser")
    private String checkOutUser;

    @Column(name = "dataState")
    private String dataState;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;

    @Column(name = "lastEventComment")
    private String lastEventComment;
}
