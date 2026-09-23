package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkId implements Serializable {

    private String factoryName;
    private Long routeLinkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WcsRouteLinkId that = (WcsRouteLinkId) o;
        return Objects.equals(factoryName, that.factoryName) &&
                Objects.equals(routeLinkId, that.routeLinkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryName, routeLinkId);
    }
}
