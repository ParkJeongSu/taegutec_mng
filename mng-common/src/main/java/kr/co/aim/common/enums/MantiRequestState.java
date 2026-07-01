package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MantiRequestState implements MetaDataEnum {
    WAIT("WAIT"),
    COMPLETED("COMPLETED"),
    TIMEOUT("TIMEOUT");
    private final String value;
}