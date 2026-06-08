package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportJobState implements MetaDataEnum {
    REQUESTED("REQUESTED"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    STARTED("STARTED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    TERMINATED("TERMINATED");
    private final String value;
}
