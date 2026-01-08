package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MNGProcessName implements MetaDataEnum {
    PEX("PEX"),
    TEX("TEX"),
    SCHEDULER("SCHEDULER");
    private final String value;
}