package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortDetailType implements MetaDataEnum {
    OUT_OF_RACK("OutOfRack"),
    WORKSTATION("Workstation"),
    INBOUND("Inbound"),
    TUNNEL("Tunnel"),
    IN_OF_RACK("InOfRack");
    private final String value;
}
