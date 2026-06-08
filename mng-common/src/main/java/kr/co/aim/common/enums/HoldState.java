package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HoldState implements MetaDataEnum {
    ON_HOLD("ON_HOLD"),
    NOT_ON_HOLD("NOT_ON_HOLD");
    private final String value;
}
