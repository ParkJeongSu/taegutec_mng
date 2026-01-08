package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionDetailType implements MetaDataEnum {
    SAMPLE("Sample"),
    SOURCE_MONITOR("SourceMonitor"),
    MONITOR("Monitor"),
    DUMMY("Dummy"),
    SIDE_DUMMY("SideDummy");
    private final String value;
}
