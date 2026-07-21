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
@Table(name = "PORT_DEF", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class PortDefEntity {

    @Id
    @Column(name="ID")
    private Long id;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PORT_NAME")
    private String portName;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "PORT_NUMBER")
    private Integer portNumber;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRANSPORT_MODE")
    private String transportMode;

    @Column(name = "PORT_TYPE")
    private String portType;

    @Column(name = "DETAIL_PORT_TYPE")
    private String detailPortType;

    @Column(name = "PORT_USE_TYPE")
    private String portUseType;

    @Column(name = "PORT_ROLE_TYPE")
    private String portRoleType;

    @Column(name = "WORK_CENTER_NAME")
    private String workCenterName;

    @Column(name = "LOCATION_ID")
    private String locationId;

    @Column(name = "CONNECTED_EQUIPMENT_NAME")
    private String connectedEquipmentName;

    @Column(name = "CONNECTED_PORT_NAME")
    private String connectedPortName;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
