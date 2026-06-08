package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocTypeId implements MetaDataEnum {
    OUTBOUND(11L),
    INBOUND(12L),
    RELOCATION(15L),
    CONFIRMATION(16L);
    private final Long value;
}
