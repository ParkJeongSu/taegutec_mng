package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteNodeUpdateCommand {

    private TransactionInfo transactionInfo;

    private String bayId;
    private String controllerType;
    private String craneId;
    private String description;
    private String equipmentId;
    private String nodeEquipmentType;
    private String nodeId;
    private String nodeName;
    private Integer nodeSeq;
    private String rerouteType;
    private String routeNodeType;
    private String unitId;
    private String useYn;
}
