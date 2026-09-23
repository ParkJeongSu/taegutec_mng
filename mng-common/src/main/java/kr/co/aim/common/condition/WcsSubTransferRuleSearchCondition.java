package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRuleSearchCondition {

    private String factoryName;
    private String equipmentName;
    private String moduleName;
    private Long routeLinkId;
    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;
}
