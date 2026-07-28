package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionStatus implements MetaDataEnum {
    ALLOCATED("ALLOCATED"),
    WAIT("WAIT"),
    CONSUMED("CONSUMED"),
    COMPLETED("COMPLETED");
    private final String value;
}
