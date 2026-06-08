package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType implements MetaDataEnum {
    GLASS("GLASS"),
    PANEL("PANEL"),
    WAFER("WAFER");
    private final String value;
}
