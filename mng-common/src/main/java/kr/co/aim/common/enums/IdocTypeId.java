package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocTypeId implements MetaDataEnum {
    Inbound(11L),
    Outbound(12L),
    Relocation(15L),
    Confirmation(16L);
    private final Long value;
}
