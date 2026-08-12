package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LotState implements MetaDataEnum {
    WIP("WIP"),
    STOCK("STOCK"),
    CREATED("CREATED"),
    RELEASED("RELEASED"),
    SCRAPPED("SCRAPPED"),
    SHIPPED("SHIPPED");
    private final String value;
}
