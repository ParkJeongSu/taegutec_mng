package kr.co.aim.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;



@Getter
@Setter // MapStruct와 JPA 바인딩을 위해 Setter 제공 (또는 @Builder/@AllArgsConstructor)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합키 필수 항목
public class IdAvailabilityHourly implements Serializable {
    private String statDate;
    private String statHour;
    private Long equipmentId;
}
