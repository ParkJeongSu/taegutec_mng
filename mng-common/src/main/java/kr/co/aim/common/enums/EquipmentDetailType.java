package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentDetailType implements MetaDataEnum {
    // 대구텍 상세 설비 타입
    INCOME("INCOME"),
    DOPING("DOPING"),
    REDUCTION("REDUCTION"),
    PURIFICATION("PURIFICATION"),
    SCREEN("SCREEN"),
    BLENDING("BLENDING"),
    MIXING("MIXING"),
    CARBURIZATION("CARBURIZATION"),
    DEAGGLOMERATION("DEAGGLOMERATION"),
    PACKING("PACKING"),
    MAGAZINE("MAGAZINE"),
    DISPENSER("DISPENSER"),
    OTHERS("OTHERS"),
    // 대구텍 상세 설비 타입
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
