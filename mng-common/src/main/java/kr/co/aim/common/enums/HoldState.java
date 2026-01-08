package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HoldState implements MetaDataEnum {
    ON_HOLD("OnHold"),
    NOT_ON_HOLD("NotOnHold");
    private final String value;
}
