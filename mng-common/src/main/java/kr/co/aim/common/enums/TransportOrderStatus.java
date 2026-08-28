package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportOrderStatus implements MetaDataEnum {
    CREATED("CREATED"),
    REQUESTED("REQUESTED"),
    ACCEPTED("ACCEPTED"),
    STARTED("STARTED"),
    REJECTED("REJECTED"),
    COMPLETED("COMPLETED"),
    CANCEL_COMPLETED("CANCEL_COMPLETED"),
    TERMINATED("TERMINATED");
    private final String value;
}
