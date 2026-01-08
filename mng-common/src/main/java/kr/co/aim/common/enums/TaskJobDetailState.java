package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskJobDetailState implements MetaDataEnum {
    WAIT("Wait"),
    RUN("Run"),
    END("End"),
    CANCELED("Canceled");
    private final String value;
}
