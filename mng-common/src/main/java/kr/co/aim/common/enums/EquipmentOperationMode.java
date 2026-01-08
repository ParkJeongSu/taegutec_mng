package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentOperationMode implements MetaDataEnum {
    MIXING("Mixing"),
    MERGE("Merge"),
    SORTER("Sorter"),
    CHANGER("Changer");

    public static boolean isExist(String operationMode) {
        for (EquipmentOperationMode mode : EquipmentOperationMode.values()) {
            if (mode.name().equalsIgnoreCase(operationMode)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }
    private final String value;
}
