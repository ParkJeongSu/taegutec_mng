package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNodeId implements Serializable {

    private String factoryName;
    private Long routeNodeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WcsRouteNodeId that = (WcsRouteNodeId) o;
        return Objects.equals(factoryName, that.factoryName) &&
                Objects.equals(routeNodeId, that.routeNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryName, routeNodeId);
    }
}
