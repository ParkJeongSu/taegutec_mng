package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionDetailType implements MetaDataEnum {
    SAMPLE("SAMPLE"),
    SOURCE_MONITOR("SOURCE_MONITOR"),
    MONITOR("MONITOR"),
    DUMMY("DUMMY"),
    SIDE_DUMMY("SIDE_DUMMY");
    private final String value;
}
