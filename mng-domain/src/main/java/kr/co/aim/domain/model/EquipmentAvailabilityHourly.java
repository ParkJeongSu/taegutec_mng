package kr.co.aim.domain.model;


import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentAvailabilityHourly {

    private IdAvailabilityHourly id;
    private String equipmentName;
    private Integer runDurationSec;
    private Integer idleDurationSec;
    private Integer downDurationSec;
    private Integer pmDurationSec;
    private Integer alarmCount;
    private LocalDateTime createTime;

    public EquipmentAvailabilityHourly(IdAvailabilityHourly id, String equipmentName) {
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