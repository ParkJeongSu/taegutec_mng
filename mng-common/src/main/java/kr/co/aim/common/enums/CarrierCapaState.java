package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierCapaState implements MetaDataEnum {
    EMPTY("Empty"),
    PARTIAL("Partial"),
    FULL("Full");
    private final String value;
}
