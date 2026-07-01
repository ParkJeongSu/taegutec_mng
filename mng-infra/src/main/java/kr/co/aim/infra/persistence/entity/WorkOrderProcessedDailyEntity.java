package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "STAT_WORK_ORDER_PROCESSED_DAILY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class WorkOrderProcessedDailyEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "STAT_DATE", length = 10, nullable = false)
    private String statDate;

    @Column(name = "TOTAL_PROCESSED_COUNT")
    private Integer totalProcessedCount;

    @Column(name = "AVG_PROCESSED_TIME")
    private Integer avgProcessedTime;

    @Column(name = "TOTAL_QUANTITY")
    private BigDecimal totalQuantity;

}