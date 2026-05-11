package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductionOrderState implements MetaDataEnum {
    CREATED("Created"),
    REQUESTED("Requested"),
    RELEASED("Released"),
    COMPLETED("Completed");
    private final String value;
}
