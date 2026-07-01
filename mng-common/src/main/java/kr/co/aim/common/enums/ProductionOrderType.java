package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderType implements MetaDataEnum {
    MATERIAL_INBOUND("MATERIAL_INBOUND"),
    UNPACKING("UNPACKING"),
    PRODUCTION_ISSUE("PRODUCTION_ISSUE"),
    PRODUCTION("PRODUCTION"),
    RRN_REPLY("RRN_REPLY"),
    MOVE_RRN("MOVE_RRN"),
    ENTER_TO_STOCK("ENTER_TO_STOCK"),
    OUTBOUND("OUTBOUND"),
    NEXT_ROUTING("NEXT_ROUTING"),
    CHANGE_ROUTING("CHANGE_ROUTING"),
    PACKING_ISSUE("PACKING_ISSUE"),
    PACKING("PACKING");
    private final String value;
}
