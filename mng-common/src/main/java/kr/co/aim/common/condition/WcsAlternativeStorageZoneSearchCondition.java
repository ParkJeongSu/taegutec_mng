package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZoneSearchCondition {

    private String factoryName;
    private String sourceZoneName;
    private String alternativeZoneName;
    private Integer priority;
    private String description;
    private String useYn;
}
