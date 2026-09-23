package kr.co.aim.infra.persistence.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleId implements Serializable {

    private String equipmentName;
    private String factoryName;
    private String moduleName;
    private Long routeLinkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WcsSubTransferRuleId that = (WcsSubTransferRuleId) o;
        return Objects.equals(equipmentName, that.equipmentName) &&
                Objects.equals(factoryName, that.factoryName) &&
                Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(routeLinkId, that.routeLinkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentName, factoryName, moduleName, routeLinkId);
    }
}
