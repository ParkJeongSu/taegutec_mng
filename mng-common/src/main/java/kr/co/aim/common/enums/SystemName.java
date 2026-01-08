package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemName implements MetaDataEnum {
    MNG("MNG"),
    WMS("WMS"),
    WCS("WCS"),
    EAS("EAS"),
    GAL("GAL"),
    PROCESS_MANAGER("PROCESS_MANAGER"),
    MANTI("MANTI");
    private final String value;
}
