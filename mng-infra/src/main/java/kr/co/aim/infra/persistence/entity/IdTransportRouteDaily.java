package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter // MapStruct와 JPA 바인딩을 위해 Setter 제공 (또는 @Builder/@AllArgsConstructor)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합키 필수 항목
public class IdTransportRouteDaily implements Serializable {

    @Column(name = "STAT_DATE", length = 10, nullable = false)
    private String statDate;

    @Column(name = "SOURCE_EQUIPMENT_NAME", length = 40, nullable = false)
    private String sourceEquipmentName;

    @Column(name = "DESTINATION_EQUIPMENT_NAME", length = 40, nullable = false)
    private String destinationEquipmentName;

}