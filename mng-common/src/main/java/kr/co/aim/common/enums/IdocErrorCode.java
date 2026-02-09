package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocErrorCode implements MetaDataEnum {
    Init("0"),
    temporarilyParked("14"),
    Parked("50"),
    Processed("60"),
    Error("99");
    private final String value;
}
