package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ALARM_ACTION")
public class AlarmActionEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ALARM_ACTION_NAME")
    private String alarmActionName;

    @Column(name = "ACTION_TYPE")
    private String actionType;

    @Column(name = "ALARM_DEF_ID")
    private Long alarmDefId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
