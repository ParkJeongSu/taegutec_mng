package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LotProcessState implements MetaDataEnum {
    WAIT("Wait"),
    RESERVE("Reserve"),
    RUN("Run");
    private final String value;
}
