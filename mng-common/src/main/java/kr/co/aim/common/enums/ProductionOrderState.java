package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderState implements MetaDataEnum {
    CREATED("CREATED"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PROCESS_REQUEST("PROCESS_REQUEST"),
    PROCESS_COMPLETED("PROCESS_COMPLETED"),
    REQUESTED("REQUESTED"),
    RELEASED("RELEASED"),
    COMPLETED("COMPLETED");
    private final String value;
}
