package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType implements MetaDataEnum {
    SPC("SPC"),
    OIC("OIC"),
    FDC("FDC"),
    EQP("EQP");
    private final String value;
}
