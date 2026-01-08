package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "ALARM_DEF_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class AlarmDefHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ALARM_DEF_NAME")
    private String alarmDefName;

    @Column(name = "ALARM_TYPE")
    private String alarmType;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ALARM_LEVEL")
    private String alarmLevel;

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
