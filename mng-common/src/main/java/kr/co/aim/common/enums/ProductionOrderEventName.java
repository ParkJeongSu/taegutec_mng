package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderEventName implements MetaDataEnum {
    CREATED("Created"),
    ARRIVED("Arrived"),
    DOWNLOAD("Download"),
    STARTED("Started"),
    MATERIAL_INPUT_EQP("Material_Input_EQP"),
    MATERIAL_OUTPUT_EQP("Material_Output_EQP"),
    ENDED("Ended");
    private final String value;
}
