package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierCleanState implements MetaDataEnum {
    CLEAN("CLEAN"),
    DIRTY("DIRTY");
    private final String value;
}
