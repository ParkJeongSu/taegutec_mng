package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "STAT_EQP_PRODUCTIVITY_DAILY", catalog = "NEXBEMNG", schema = "dbo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Builder // ✨ MapStruct가 빌더 패턴을 인식하여 완벽히 바인딩합니다.
@AllArgsConstructor // ✨ 매핑을 유연하게 처리하기 위한 전 필드 생성자 자동 생성
public class EquipmentProductivityDailyEntity {

    @EmbeddedId
    private IdProductivityDaily id;

    @Column(name = "TOTAL_PROCESSED_COUNT")
    private Integer totalProcessedCount;

    @Column(name = "TOTAL_PROCESSED_QUANTITY")
    private Integer totalProcessedQuantity;

    @Column(name = "OK_PROCESSED")
    private Integer okProcessed;

    @Column(name = "NG_PROCESSED")
    private Integer ngProcessed;

    @Column(name = "AVG_PROCESSED_TIME")
    private Integer avgProcessedTime;
}