package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LotState implements MetaDataEnum {
    CREATED("Created"),
    RELEASED("Released"),
    SCRAPPED("Scrapped"),
    SHIPPED("Shipped");
    private final String value;
}
