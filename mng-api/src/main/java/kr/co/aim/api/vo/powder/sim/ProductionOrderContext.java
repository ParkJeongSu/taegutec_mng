package kr.co.aim.api.vo.powder.sim;

import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductionOrderContext {
    private final ProductionOrder productionOrder; // MSSQL 엔티티 (조회 시점에 없을 수 있음)
    private final IdocPEntity idoc;                     // DB2 IDOC
    private final H2OrderMPEntity master;               // DB2 Master
    private final H2OrderDPEntity detail;        // DB2 Details
    private final List<H2PartMPEntity> partList;

    private final String partId;
    private final String lotName;
    private final String itemName;
    private final String carrierName;
    private final String equipmentName;
    private final BigDecimal actualQuantity;
    private final BigDecimal planQuantity;
    private final BigDecimal releasedQuantity;
    private final BigDecimal startedQuantity;
    private final BigDecimal endedQuantity;
    private final BigDecimal scrappedQuantity;
    private final BigDecimal missingQuantity;
    private final BigDecimal surplusQuantity;



}
