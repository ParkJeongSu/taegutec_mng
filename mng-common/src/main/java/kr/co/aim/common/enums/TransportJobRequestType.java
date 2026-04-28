package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportJobRequestType implements MetaDataEnum {
    UI("UI"),
    EQP("EQP"),
    WCS("WCS"),
    GAL("GAL");
    private final String value;
}
