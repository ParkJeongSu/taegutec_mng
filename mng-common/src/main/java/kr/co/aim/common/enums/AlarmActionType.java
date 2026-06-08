package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmActionType implements MetaDataEnum {
    MAIL("MAIL"),
    MESSAGE("MESSAGE"),
    OP_CALL("OP_CALL");

    private final String value;
}
