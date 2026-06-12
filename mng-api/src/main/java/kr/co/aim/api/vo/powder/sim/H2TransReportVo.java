package kr.co.aim.api.vo.powder.sim;

import kr.co.aim.common.enums.GALProductionStatus;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H2TransReportVo {

    private Long productionOrderId;;
    private String messageName;
    private Port port;
    private PortDef portDef;

    // 1. 상태 및 식별 정보 (핵심)
    private GALProductionStatus status; // 보고할 상태 (Enum)
    private String orderId;
    private String orderLineNumber;
    private Long h2OrderDpLineId;
    private String carrierName;         // 캐리어/용기 ID

    private BigDecimal actQty;
    private BigDecimal missQty;
    private BigDecimal surpQty;
    private String resultStat;
    private String errReason;

    // 4. 원천 데이터 엔티티 (DB 저장용 참조)
    private IdocPEntity sourceIdoc;     // 기준이 된 IDOC
    private H2OrderMPEntity master;     // 주문 마스터
    private H2OrderDPEntity detail;        // DB2 Details
    private IdocPEntity newIdoc;        // new IDOC

    private String partId;
    private Long refLineId;

}
