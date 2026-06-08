package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LotProcessState implements MetaDataEnum {
    WAIT("WAIT"),
    RESERVE("RESERVE"),
    RUN("RUN");
    private final String value;
}
