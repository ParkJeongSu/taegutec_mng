package kr.co.aim.infra.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "STAT_EQP_AVAILABILITY_HOURLY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@Builder // ✨ MapStruct가 빌더 패턴을 인식하여 완벽히 바인딩합니다.
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentAvailabilityHourlyEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "STAT_DATE", length = 10, nullable = false)
    private String statDate;

    @Column(name = "STAT_HOUR", length = 2, nullable = false)
    private String statHour;

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

}