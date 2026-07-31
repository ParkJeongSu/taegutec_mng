package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderState implements MetaDataEnum {
    CREATED("CREATED"),
    ALLOCATE_REQUEST("ALLOCATE_REQUEST"),
    ALLOCATE_COMPLETED("ALLOCATE_COMPLETED"),
    REQUESTED("REQUESTED"),
    RELEASED("RELEASED"),
    COMPLETED("COMPLETED");
    private final String value;
}
