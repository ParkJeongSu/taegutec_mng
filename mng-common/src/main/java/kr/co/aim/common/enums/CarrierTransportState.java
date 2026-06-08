package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierTransportState implements MetaDataEnum {
    MOVING("MOVING"),
    IN_EQP("IN_EQP"),
    ON_PORT("ON_PORT"),
    IN_WAREHOUSE("IN_WAREHOUSE"),
    IN_AREA("IN_AREA"),
    IN_STK("IN_STK");
    private final String value;
}
