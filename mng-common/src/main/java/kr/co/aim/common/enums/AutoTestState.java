package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AutoTestState implements MetaDataEnum {
    OUTBOUND_REQUESTED("OUTBOUND_REQUESTED"),
    RELOCATION_REQUESTED1("RELOCATION_REQUESTED1"),
    RELOCATION_REQUESTED2("RELOCATION_REQUESTED2"),
    INBOUND_REQUESTED("INBOUND_REQUESTED");
    private final String value;
}
