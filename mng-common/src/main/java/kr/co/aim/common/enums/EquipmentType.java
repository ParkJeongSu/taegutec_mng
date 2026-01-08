package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentType implements MetaDataEnum {
    PROCESS("Process"),
    WAREHOUSE("Warehouse"),
    SORTER("Sorter"),
    STOCKER("Stocker");
    private final String value;
}
