package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter // MapStruct와 JPA 바인딩을 위해 Setter 제공 (또는 @Builder/@AllArgsConstructor)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합키 필수 항목
public class IdAvailabilityHourly implements Serializable {

    @Column(name = "STAT_DATE", length = 10, nullable = false)
    private String statDate;

    @Column(name = "STAT_HOUR", length = 2, nullable = false)
    private String statHour;

    @Column(name = "EQUIPMENT_ID", nullable = false)
    private Long equipmentId;

}
