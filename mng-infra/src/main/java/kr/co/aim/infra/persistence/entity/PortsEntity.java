package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "PORTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class PortsEntity {
    @Id
    @Column(name="ID")
    private Long id;

    @Column(name="EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name="PORT_NAME")
    private String portName;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CONNECTED_STOCKER")
    private String connectedStocker;

    @Column(name="TRANSPORT_MODE")
    private String transportMode;

    @Column(name="PORT_STATE")
    private String portState;

    @Column(name="RESOURCE_STATE")
    private String resourceState;

    @Column(name="TRANSPORT_STATE")
    private String transportState;

    @Column(name="CARRIER_NAME")
    private String carrierName;

    @Column(name="TRANSPORT_JOB_ID")
    private Long transportJobId;

    @Column(name="EVENT_NAME")
    private String eventName;

    @Column(name="EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name="EVENT_USER")
    private String eventUser;

    @Column(name="EVENT_COMMENT")
    private String eventComment;
}
