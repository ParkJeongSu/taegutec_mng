package kr.co.aim.infra.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "STAT_EQP_AVAILABILITY_HOURLY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@Builder // ✨ MapStruct가 빌더 패턴을 인식하여 완벽히 바인딩합니다.
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentAvailabilityHourlyEntity {

    @EmbeddedId
    private IdAvailabilityHourly id;

    @Column(name = "EQUIPMENT_NAME", length = 40, nullable = false)
    private String equipmentName;

    @Column(name = "RUN_DURATION_SEC")
    private Integer runDurationSec;

    @Column(name = "IDLE_DURATION_SEC")
    private Integer idleDurationSec;

    @Column(name = "DOWN_DURATION_SEC")
    private Integer downDurationSec;

    @Column(name = "PM_DURATION_SEC")
    private Integer pmDurationSec;

    @Column(name = "ALARM_COUNT")
    private Integer alarmCount;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    public EquipmentAvailabilityHourlyEntity(IdAvailabilityHourly id, String equipmentName) {
        this.id = id;
        this.equipmentName = equipmentName;
        this.runDurationSec = 0;
        this.idleDurationSec = 0;
        this.downDurationSec = 0;
        this.pmDurationSec = 0;
        this.alarmCount = 0;
        this.createTime = LocalDateTime.now();
    }

    // 통계치 누적을 위한 편의 메서드
    public void addDurations(int run, int idle, int down, int pm, int alarm) {
        this.runDurationSec = (this.runDurationSec == null ? 0 : this.runDurationSec) + run;
        this.idleDurationSec = (this.idleDurationSec == null ? 0 : this.idleDurationSec) + idle;
        this.downDurationSec = (this.downDurationSec == null ? 0 : this.downDurationSec) + down;
        this.pmDurationSec = (this.pmDurationSec == null ? 0 : this.pmDurationSec) + pm;
        this.alarmCount = (this.alarmCount == null ? 0 : this.alarmCount) + alarm;
    }
}