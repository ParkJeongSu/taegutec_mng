package kr.co.aim.api.vo.powder.ops;

import kr.co.aim.common.enums.GALProductionStatus;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H2TransPReportVo {

    private GALProductionStatus status;  // 보고할 상태 (Enum)
    private String orderId;
    private String orderLineNumber;
    private String carrierName;         // 캐리어/용기 ID
    private Long idocId;
    private IdocPEntity newIdoc;
    private Integer lineNo;
    private Integer lot;
    private String galKey;
    private String carrierId;
    private Integer currRrn;
    private Integer nextRrn;
    private BigDecimal actQty;
    private BigDecimal missQty;
    private BigDecimal surpQty;
    private String resultStat;
    private String errReason;
    private LocalDateTime eventDt;
    private Long h2ordLineId;
    private String cPartId;
    private Long refLineId;
    private Long mngKey;
}
