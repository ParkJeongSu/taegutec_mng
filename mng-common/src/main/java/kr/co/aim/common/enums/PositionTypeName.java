package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PositionTypeName implements MetaDataEnum {
    PORT("PORT"),
    SHELF("SHELF"),
    CRANE("CRANE"),
    CONVEYOR("CONVEYOR");
    private final String value;
}
