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
@Table(name = "PORT_DEF")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class PortDefEntity {

    @EmbeddedId
    private PortDefId id; // 복합키 클래스를 ID로 사용

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PORT_TYPE")
    private String portType;

    @Column(name = "DETAIL_PORT_TYPE")
    private String detailPortType;

    @Column(name = "PORT_USE_TYPE")
    private String portUseType;

    @Column(name = "WORK_CENTER_NAME")
    private String workCenterName;

    @Column(name = "LOCATION_ID")
    private String locationId;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "LAST_EVENT_NAME")
    private String lastEventName;

    @Column(name = "LAST_EVENT_TIME")
    private LocalDateTime lastEventTime;

    @Column(name = "LAST_EVENT_USER")
    private String lastEventUser;

    @Column(name = "LAST_EVENT_COMMENT")
    private String lastEventComment;
}
