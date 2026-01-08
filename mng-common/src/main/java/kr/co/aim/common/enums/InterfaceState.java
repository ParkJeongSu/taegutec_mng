package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterfaceState implements MetaDataEnum {
    CREATED("Created"),
    RECEIVED("Received"),
    CANCELED("Canceled");
    private final String value;
}
