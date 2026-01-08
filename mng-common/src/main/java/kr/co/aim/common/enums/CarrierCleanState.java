package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierCleanState implements MetaDataEnum {
    CLEAN("Clean"),
    DIRTY("Dirty");
    private final String value;
}
