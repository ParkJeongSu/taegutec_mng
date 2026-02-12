package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "STAT_EQP_AVAILABILITY_HOURLY")
@IdClass(StatEQPProductivityDailyId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatEQPProductivityDailyEntity {
    @Id
    @Column(name="STAT_DATE")
    private String statDate;

    @Id
    @Column(name="EQUIPMENT_ID")
    private Long equipmentId;

    @Id
    @Column(name="EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name="TOTAL_PROCESSED_COUNT")
    private Integer totalProcessedCount;

    @Column(name="TOTAL_PROCESSED_QUANTITY")
    private Integer totalProcessedQuantity;

    @Column(name="OK_PROCESSED")
    private Integer okProcessed;

    @Column(name="NG_PROCESSED")
    private Integer ngProcessed;

    @Column(name="AVG_PROCESSED_TIME")
    private Integer avgProcessedTime;
}
