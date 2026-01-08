package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentDetailType implements MetaDataEnum {
    CVD("CVD"),
    CD("CD"),
    OVERLAY("Overlay"),
    THICKNESS("Thickness"),
    PHOTO("Photo"),
    STEPPER("Stepper"),
    TRACK("Track"),
    FURNACE("Furnace"),
    ETC("Etc"),
    DUMMY("Dummy"),
    EPM("EPM"),
    TEST("TEST"),
    FINAL_QC("FinalQC"),
    MERGE("Merge");
    private final String value;
}
