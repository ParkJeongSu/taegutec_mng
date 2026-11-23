package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AutoTestState implements MetaDataEnum {
    OUTBOUND_REQUESTED("OUTBOUND_REQUESTED"),
    INBOUND_REQUESTED("INBOUND_REQUESTED");
    private final String value;
}
