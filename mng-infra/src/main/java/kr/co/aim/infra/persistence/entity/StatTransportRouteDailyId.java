package kr.co.aim.infra.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatTransportRouteDailyId implements Serializable {
    private String statDate;
    private String sourceEquipmentName;
    private String destinationEquipmentName;

    // equals 재정의 (람다 미사용)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatTransportRouteDailyId that = (StatTransportRouteDailyId) o;
        return Objects.equals(statDate, that.statDate) &&
                Objects.equals(sourceEquipmentName, that.sourceEquipmentName) &&
                Objects.equals(destinationEquipmentName, that.destinationEquipmentName);
    }

    // hashCode 재정의
    @Override
    public int hashCode() {
        return Objects.hash(statDate, sourceEquipmentName, destinationEquipmentName);
    }
}
