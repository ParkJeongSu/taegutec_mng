package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionType implements MetaDataEnum {
    PRODUCTION("Production"),
    ENGINEER("Engineer"),
    DEVELOP("Develop");
    private final String value;
}
