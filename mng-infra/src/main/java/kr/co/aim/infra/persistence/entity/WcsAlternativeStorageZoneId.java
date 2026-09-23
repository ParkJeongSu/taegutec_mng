package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneId implements Serializable {

    private String alternativeZoneName;
    private String factoryName;
    private Integer priority;
    private String sourceZoneName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WcsAlternativeStorageZoneId that = (WcsAlternativeStorageZoneId) o;
        return Objects.equals(alternativeZoneName, that.alternativeZoneName) &&
                Objects.equals(factoryName, that.factoryName) &&
                Objects.equals(priority, that.priority) &&
                Objects.equals(sourceZoneName, that.sourceZoneName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alternativeZoneName, factoryName, priority, sourceZoneName);
    }
}
