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
@Table(name = "W_ALARM_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsAlarmHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "alarmId")
    private String alarmId;

    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "layerNumber")
    private Integer layerNumber;

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

    @Column(name = "systemAlarmFlag")
    private Boolean systemAlarmFlag;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
