package kr.co.aim.api.vo.powder.ops;

import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PowderEventQueueReportVo {

    private final String messageName;
    private final TransactionInfo tx;
    private final EquipmentDef equipmentDef;
    private final Equipment equipment;
    private final PortDef portDef;
    private final Port port;
    private final String carrierName;
    // orderType
    //    MATERIAL_INBOUND : 원자재 입고
    //    UNPACKING : 해포
    //    PRODUCTION_ISSUE : 원자재 이슈 (최초 container 에 담는 order Type)
    //    NEXT_ROUTING : 다음 공정
    //    PRODUCTION : 조업
    //    RRN_REPLY : MNG 에서 문의한 RRN의 REPLY
    //    MOVE_RRN : 단일 container 혹은 다수의 container rrn(공정) 변경
    //    PACKING :  원자재 패킹
    //    ENTER_TO_STOCK : CONTAINER 의 LOT_STATUS WIP -> STOCK 로 변경
    //    OUTBOUND : 출고
    //    CYCLE_COUNT : 재고조사
    private final String orderType;
    private final String resultCode;
    private final String resultMessage;

    private final String equipmentName;
    private final String recipeName;
    private final String lotName;
    private final String itemName;
    private final String orderId;
    private final String orderLineNumber;
    private final Long productionOrderId;
    private final String productionStatus;
    private final String processStatus;
    private final BigDecimal quantity;
    private final BigDecimal scrapQuantity;
    private final Long mngKey;

}
