package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckoutState implements MetaDataEnum {
    CHECKED_IN("CheckedIn"),
    CHECKED_OUT("CheckedOut");
    private final String value;
}
