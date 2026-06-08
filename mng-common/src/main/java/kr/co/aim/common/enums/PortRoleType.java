package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortRoleType implements MetaDataEnum {
    INTERNAL("INTERNAL"),
    EXTERNAL("EXTERNAL"),
    WCS("WCS"),
    EAS("EAS");
    private final String value;


}
