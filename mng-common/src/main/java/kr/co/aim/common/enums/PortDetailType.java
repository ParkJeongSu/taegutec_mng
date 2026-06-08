package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortDetailType implements MetaDataEnum {
    OUT_OF_RACK("OUT_OF_RACK"),
    WORKSTATION("WORKSTATION"),
    INBOUND("INBOUND"),
    TUNNEL("TUNNEL"),
    BOTH_OF_RACK("BOTH_OF_RACK"),
    IN_OF_RACK("IN_OF_RACK");
    private final String value;
}
