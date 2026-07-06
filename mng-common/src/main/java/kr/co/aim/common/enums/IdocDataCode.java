package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocDataCode implements MetaDataEnum {
    DATA_CODE01("01"),
    INBOUND("20"),
    ELSE("40");
    private final String value;
}
