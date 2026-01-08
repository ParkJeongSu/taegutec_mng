package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierState implements MetaDataEnum {
    CREATED("Created"),
    SCRAPPED("Scrapped"),
    IN_PROCESSING("InProcessing");
    private final String value;
}
