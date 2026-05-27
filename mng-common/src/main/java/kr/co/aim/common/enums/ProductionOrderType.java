package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderType implements MetaDataEnum {
    INBOUND("Inbound"),
    UNPACK("Unpack"),
    PRODUCTION("Production"),
    PACKING("Packing");
    private final String value;
}
