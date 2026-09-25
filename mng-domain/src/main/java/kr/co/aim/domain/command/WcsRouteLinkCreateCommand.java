package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLinkCreateCommand {

    private TransactionInfo transactionInfo;

    private String factoryName;
    private Long routeLinkId;
    private String description;
    private String fromNodeId;
    private Integer length;
    private String passYn;
    private Integer priority;
    private String processType;
    private String routeLinkType;
    private String toNodeId;
    private String usableYn;
    private String useYn;
}
