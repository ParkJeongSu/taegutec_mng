package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode // 복합키는 동등성 비교가 필수입니다.
public class PortDefId implements Serializable {
    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "portName")
    private String portName;
}
