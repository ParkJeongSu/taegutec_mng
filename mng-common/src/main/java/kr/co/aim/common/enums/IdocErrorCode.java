package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocErrorCode implements MetaDataEnum {
    Init(0L),
    temporarilyParked(14L),
    Parked(50L),
    Processed(60L),
    Error(99L);
    private final Long value;
}
