package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkOrderState implements MetaDataEnum {
    CREATED("Created"),
    RELEASED("Released"),
    COMPLETED("Completed");
    private final String value;
}
