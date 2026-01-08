package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortUseType implements MetaDataEnum {
    OK("OK"),
    RW("RW"),
    NG("NG");
    private final String value;
}
