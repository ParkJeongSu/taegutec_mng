package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "STAT_TRANSPORT_ROUTE_DAILY")
@IdClass(StatTransportRouteDailyId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatTransportRouteDailyEntity {
    @Id
    @Column(name="STAT_DATE")
    private String statDate;

    @Id
    @Column(name="SOURCE_EQUIPMENT_NAME")
    private String sourceEquipmentName;

    @Id
    @Column(name="DESTINATION_EQUIPMENT_NAME")
    private String destinationEquipmentName;

    @Column(name="TOTAL_COUNT")
    private Integer totalCount;

    @Column(name="ERROR_COUNT")
    private Integer errorCount;

    @Column(name="AVG_ACQUIRE_TIME_SEC")
    private Integer avgAcquireTimeSec;

    @Column(name="MAX_ACQUIRE_TIME_SEC")
    private Integer maxAcquireTimeSec;

    @Column(name="AVG_TRANSFER_TIME_SEC")
    private Integer avgTransferTimeSec;

    @Column(name="MAX_TRANSFER_TIME_SEC")
    private Integer maxTransferTimeSec;

    @Column(name="AVG_CYCLE_TIME_SEC")
    private Integer avgCycleTimeSec;

    @Column(name="MAX_CYCLE_TIME_SEC")
    private Integer maxCycleTimeSec;

}
