package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocErrorCode implements MetaDataEnum {
    INIT(0L),
    TEMPORARILY_PARKED(14L),
    PARKED(50L),
    PROCESSED(60L),
    ERROR(99L);
    private final Long value;
}
