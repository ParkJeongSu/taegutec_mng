package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocDataCode implements MetaDataEnum {
    INBOUND(20L),
    ELSE(40L);
    private final Long value;
}
