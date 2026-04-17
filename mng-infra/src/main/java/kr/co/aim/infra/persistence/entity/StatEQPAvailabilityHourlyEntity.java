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
@Table(name = "STAT_EQP_AVAILABILITY_HOURLY", catalog = "NEXBEEAS", schema = "dbo")
@IdClass(StatEQPAvailabilityHourlyId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatEQPAvailabilityHourlyEntity {
    @Id
    @Column(name="STAT_DATE")
    private String statDate;

    @Id
    @Column(name="STAT_HOUR")
    private String statHour;

    @Id
    @Column(name="EQUIPMENT_ID")
    private Long equipmentId;

    @Id
    @Column(name="EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name="RUN_DURATION_SEC")
    private Integer runDurationSec;

    @Column(name="IDLE_DURATION_SEC")
    private Integer idleDurationSec;

    @Column(name="DOWN_DURATION_SEC")
    private Integer downDurationSec;

    @Column(name="PM_DURATION_SEC")
    private Integer pmDurationSec;

    @Column(name="ALARM_COUNT")
    private Integer alarmCount;

    @Column(name="CREATE_TIME")
    private LocalDateTime createTime;
}
