package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSubType implements MetaDataEnum {
    GLASS("GLASS"),
    PANEL("PANEL"),
    WAFER("WAFER"),
    CELL("CELL"),
    CHIP("CHIP");
    private final String value;
}
