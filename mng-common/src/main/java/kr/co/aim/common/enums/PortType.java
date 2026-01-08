package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortType implements MetaDataEnum {
    INPUT("Input"),
    OUTPUT("Output"),
    INPUT_OUTPUT("InputOutput");
    private final String value;
}
