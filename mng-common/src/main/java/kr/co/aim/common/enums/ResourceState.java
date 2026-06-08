package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourceState implements MetaDataEnum {
    IN_SERVICE("IN_SERVICE"),
    OUT_OF_SERVICE("OUT_OF_SERVICE");
    private final String value;
}
