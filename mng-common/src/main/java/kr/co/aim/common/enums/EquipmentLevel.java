package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentLevel implements MetaDataEnum {
    LEVEL_1("Line"),
    LEVEL_2("Machine"),
    LEVEL_3("Unit"),
    LEVEL_4("SubUnit"),
    UNIT("Unit"),
    GROUP("Group");
    private final String value;
}
