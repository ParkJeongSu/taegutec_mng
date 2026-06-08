package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckoutState implements MetaDataEnum {
    CHECKED_IN("CHECKED_IN"),
    CHECKED_OUT("CHECKED_OUT");
    private final String value;
}
