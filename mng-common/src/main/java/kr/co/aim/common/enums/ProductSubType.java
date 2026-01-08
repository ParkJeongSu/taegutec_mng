package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSubType implements MetaDataEnum {
    GLASS("Glass"),
    PANEL("Panel"),
    WAFER("Wafer"),
    CELL("Cell"),
    CHIP("Chip");
    private final String value;
}
