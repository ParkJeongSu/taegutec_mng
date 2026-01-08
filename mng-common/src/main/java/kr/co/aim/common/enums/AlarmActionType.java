package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmActionType implements MetaDataEnum {
    MAIL("mail"),
    MESSAGE("message"),
    OP_CALL("opCall");

    private final String value;
}
