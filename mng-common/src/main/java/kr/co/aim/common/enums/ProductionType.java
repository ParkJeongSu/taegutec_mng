package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionType implements MetaDataEnum {
    PRODUCTION("PRODUCTION"),
    ENGINEER("ENGINEER"),
    DEVELOP("DEVELOP");
    private final String value;
}
