package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocTypeId implements MetaDataEnum {
    CANCEL_OUTBOUND(50L),
    CANCEL_INBOUND(52L),
    CANCEL_RELOCATION(54L),
    OUTBOUND(11L),
    INBOUND(12L),
    RELOCATION(15L),
    CONFIRMATION(16L);
    private final Long value;
}
