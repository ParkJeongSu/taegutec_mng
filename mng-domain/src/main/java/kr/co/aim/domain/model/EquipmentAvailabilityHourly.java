package kr.co.aim.domain.model;


import kr.co.aim.common.Utils.FormatUtils;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EquipmentAvailabilityHourly {

    private Long id;
    private String statDate;
    private String statHour;
    private String equipmentName;
    private Integer runDurationSec;
    private Integer idleDurationSec;
    private Integer downDurationSec;
    private Integer pmDurationSec;
    private Integer alarmCount;
    private LocalDateTime createTime;

    public EquipmentAvailabilityHourly(Long id, String equipmentName,LocalDateTime eventTime) {
        this.id = id;
        this.statDate = eventTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.statHour = eventTime.format(FormatUtils.TIME_FORMATTER);
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