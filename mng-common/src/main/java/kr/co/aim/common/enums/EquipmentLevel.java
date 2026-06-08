package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentLevel implements MetaDataEnum {
    LEVEL_1("LINE"),
    LEVEL_2("MACHINE"),
    LEVEL_3("UNIT"),
    LEVEL_4("SUBUNIT"),
    UNIT("UNIT"),
    GROUP("GROUP");
    private final String value;
}
