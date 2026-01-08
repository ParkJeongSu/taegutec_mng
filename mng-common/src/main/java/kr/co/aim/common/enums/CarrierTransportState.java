package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierTransportState implements MetaDataEnum {
    MOVING("Moving"),
    IN_EQP("InEQP"),
    ON_PORT("OnPort"),
    IN_WAREHOUSE("InWarehouse"),
    IN_AREA("InArea"),
    IN_STK("InSTK");
    private final String value;
}
