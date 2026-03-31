package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportOrderType implements MetaDataEnum {
    INBOUND("I"),
    OUTBOUND("O"),
    RELOCATION("R"),
    TRANSPORT("T");
    private final String value;
}
