package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortTransportMode implements MetaDataEnum {
    AUTO("AUTO"),
    MANUAL("MANUAL");
    private final String value;
}
