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
@Table(name = "ALARM", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsAlarmId.class)
public class WcsAlarmEntity {

    @Id
    @Column(name = "alarmId")
    private String alarmId;

    @Id
    @Column(name = "equipmentName")
    private String equipmentName;

    @Id
    @Column(name = "factoryName")
    private String factoryName;

    @Id
    @Column(name = "layerNumber")
    private Integer layerNumber;

    @Id
    @Column(name = "layerType")
    private String layerType;

    @Column(name = "alarmLevel")
    private String alarmLevel;

    @Column(name = "alarmRecoveryOptions")
    private String alarmRecoveryOptions;

    @Column(name = "alarmText")
    private String alarmText;

    @Column(name = "layerName")
    private String layerName;

    @Column(name = "lastEventComment")
    private String lastEventComment;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;
}
