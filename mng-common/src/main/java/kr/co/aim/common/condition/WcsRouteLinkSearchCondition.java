package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkSearchCondition {

    private String factoryName;
    private Long routeLinkId;
    private String description;
    private String fromNodeId;
    private String toNodeId;
    private String routeLinkType;
    private String processType;
    private String passYn;
    private String usableYn;
    private String useYn;
    private Integer priority;
}
