package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "STAT_EQP_PRODUCTIVITY_DAILY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Builder // ✨ MapStruct가 빌더 패턴을 인식하여 완벽히 바인딩합니다.
@AllArgsConstructor // ✨ 매핑을 유연하게 처리하기 위한 전 필드 생성자 자동 생성
public class EquipmentProductivityDailyEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "STAT_DATE", length = 10, nullable = false)
    private String statDate;

    @Column(name = "EQUIPMENT_NAME", length = 40, nullable = false)
    private String equipmentName;

    @Column(name = "TOTAL_PROCESSED_COUNT")
    private Integer totalProcessedCount;

    @Column(name = "TOTAL_PROCESSED_QUANTITY")
    private BigDecimal totalProcessedQuantity;

    @Column(name = "OK_PROCESSED")
    private BigDecimal okProcessed;

    @Column(name = "NG_PROCESSED")
    private BigDecimal ngProcessed;

    @Column(name = "AVG_PROCESSED_TIME")
    private Integer avgProcessedTime;
}