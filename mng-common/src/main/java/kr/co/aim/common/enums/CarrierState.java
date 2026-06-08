package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierState implements MetaDataEnum {
    CREATED("CREATED"),
    SCRAPPED("SCRAPPED"),
    IN_PROCESSING("IN_PROCESSING");
    private final String value;
}
