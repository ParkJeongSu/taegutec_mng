package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessStatus implements MetaDataEnum {
    WAIT("WAIT"),
    RUN("RUN"),
    COMPLETED("COMPLETED");
    private final String value;
}
