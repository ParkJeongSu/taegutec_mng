package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "STAT_TRANSPORT_ROUTE_DAILY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class TransportRouteDailyEntity {

    @EmbeddedId
    private IdTransportRouteDaily id;

    @Column(name = "TOTAL_COUNT")
    private Integer totalCount;

    @Column(name = "ERROR_COUNT")
    private Integer errorCount;

    @Column(name = "AVG_ACQUIRE_TIME_SEC")
    private Integer avgAcquireTimeSec;

    @Column(name = "MAX_ACQUIRE_TIME_SEC")
    private Integer maxAcquireTimeSec;

    @Column(name = "AVG_TRANSFER_TIME_SEC")
    private Integer avgTransferTimeSec;

    @Column(name = "MAX_TRANSFER_TIME_SEC")
    private Integer maxTransferTimeSec;

    @Column(name = "AVG_CYCLE_TIME_SEC")
    private Integer avgCycleTimeSec;

    @Column(name = "MAX_CYCLE_TIME_SEC")
    private Integer maxCycleTimeSec;

}