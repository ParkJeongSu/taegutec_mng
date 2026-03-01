package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocTypeId implements MetaDataEnum {
    Inbound("11"),
    Outbound("12"),
    Relocation("15"),
    Confirmation("16");
    private final String value;
}
