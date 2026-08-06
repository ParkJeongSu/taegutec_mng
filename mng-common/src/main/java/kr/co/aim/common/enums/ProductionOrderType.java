package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderType implements MetaDataEnum {
    PART("PART",19L),
    MATERIAL_INBOUND("MATERIAL_INBOUND",12L),
    UNPACKING("UNPACKING",13L),
    PRODUCTION_ISSUE("PRODUCTION_ISSUE",21L),
    PRODUCTION("PRODUCTION",15L),
    RRN_REPLY("RRN_REPLY",17L),
    ENTER_TO_STOCK("ENTER_TO_STOCK",51L),
    OUTBOUND("OUTBOUND",11L),
    CHANGE_RRN("CHANGE_RRN",50L),
    PACKING_ISSUE("PACKING_ISSUE",31L),
    PACKING("PACKING",18L);
    private final String value;
    private final Long code;
}
