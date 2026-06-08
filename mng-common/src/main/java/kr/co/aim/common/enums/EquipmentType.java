package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentType implements MetaDataEnum {
    PROCESS("PROCESS"),
    WAREHOUSE("WAREHOUSE"),
    SORTER("SORTER"),
    STOCKER("STOCKER");
    private final String value;
}
