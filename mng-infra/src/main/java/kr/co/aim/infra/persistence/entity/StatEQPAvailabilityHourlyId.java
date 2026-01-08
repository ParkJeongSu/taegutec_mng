package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class StatEQPAvailabilityHourlyId implements Serializable {

    private String statDate;

    private String statHour;

    private Long equipmentId;

    private String equipmentName;

    // equals 재정의 (람다 미사용)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatEQPAvailabilityHourlyId that = (StatEQPAvailabilityHourlyId) o;
        return Objects.equals(statDate, that.statDate) &&
                Objects.equals(statHour, that.statHour) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(equipmentName, that.equipmentName);
    }

    // hashCode 재정의
    @Override
    public int hashCode() {
        return Objects.hash(statDate, statHour, equipmentId, equipmentName);
    }
}
