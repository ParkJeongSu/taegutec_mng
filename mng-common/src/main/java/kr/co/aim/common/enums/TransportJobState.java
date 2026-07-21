package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportJobState implements MetaDataEnum {
    REQUESTED("REQUESTED"),
    ACCEPTED("ACCEPTED"),
    START_REQUEST("START_REQUEST"),
    STARTED("STARTED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    TERMINATED("TERMINATED");
    private final String value;
}
