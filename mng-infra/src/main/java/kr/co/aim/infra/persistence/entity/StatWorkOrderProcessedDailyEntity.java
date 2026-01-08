package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "STAT_WORK_ORDER_PROCESSED_DAILY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatWorkOrderProcessedDailyEntity {
    @Id
    @Column(name="STATE_DATE")
    private String statDate;

    @Column(name="TOTAL_PROCESSED_COUNT")
    private Integer totalProcessedCount;

    @Column(name="AVG_PROCESSED_TIME")
    private Integer avgProcessedTime;

    @Column(name="TOTAL_QUANTITY")
    private Integer totalQuantity;

}
