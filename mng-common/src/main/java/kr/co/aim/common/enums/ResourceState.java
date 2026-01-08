package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceState implements MetaDataEnum {
    IN_SERVICE("InService"),
    OUT_OF_SERVICE("OutOfService");
    private final String value;
}
