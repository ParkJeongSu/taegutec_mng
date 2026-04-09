package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportErrorCode implements MetaDataEnum {
    BIN_EMPTY("BinEmpty"),
    OVER_WEIGHT("OverWeight"),
    OVER_HEIGHT("OverHeight"),
    NO_READ("NoRead"),
    ALREADY_EXIST("AlreadyExist"),
    NO_SPACE("NoSpace"),
    ELSE("Else");
    private final String value;
}
