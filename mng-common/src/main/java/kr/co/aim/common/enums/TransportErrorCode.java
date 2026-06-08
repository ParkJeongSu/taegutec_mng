package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportErrorCode implements MetaDataEnum {
    BIN_EMPTY("BIN_EMPTY"),
    OVER_WEIGHT("OVER_WEIGHT"),
    OVER_HEIGHT("OVER_HEIGHT"),
    NO_READ("NO_READ"),
    ALREADY_EXIST("ALREADY_EXIST"),
    NO_SPACE("NO_SPACE"),
    ELSE("ELSE");
    private final String value;
}
