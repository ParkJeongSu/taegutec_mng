package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderEventName implements MetaDataEnum {
    CREATED("CREATED"),
    ARRIVED("ARRIVED"),
    DOWNLOAD("DOWNLOAD"),
    STARTED("STARTED"),
    MATERIAL_INPUT_EQP("MATERIAL_INPUT_EQP"),
    MATERIAL_OUTPUT_EQP("MATERIAL_OUTPUT_EQP"),
    ENDED("ENDED");
    private final String value;
}
