package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DetailPortType implements MetaDataEnum {
    RACK_OUT_STAGE("RACK_OUT_STAGE"),
    WORKSTATION("WORKSTATION"),
    INBOUND("INBOUND"),
    RACK_IN_STAGE("RACK_IN_STAGE"),
    TUNNEL("TUNNEL"),
    RACK_BOTH_STAGE("RACK_BOTH_STAGE"),
    CRANE_OUT_PND("CRANE_OUT_PND"),
    CRANE_IN_PND("CRANE_IN_PND"),
    CRANE_BOTH_PND("CRANE_BOTH_PND");
    private final String value;
}
