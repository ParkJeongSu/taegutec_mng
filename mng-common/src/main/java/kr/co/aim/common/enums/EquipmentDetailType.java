package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentDetailType implements MetaDataEnum {
    CVD("CVD"),
    CD("CD"),
    OVERLAY("OVERLAY"),
    THICKNESS("THICKNESS"),
    PHOTO("PHOTO"),
    STEPPER("STEPPER"),
    TRACK("TRACK"),
    FURNACE("FURNACE"),
    ETC("ETC"),
    DUMMY("DUMMY"),
    EPM("EPM"),
    TEST("TEST"),
    FINAL_QC("FINAL_QC"),
    MERGE("MERGE");
    private final String value;
}
