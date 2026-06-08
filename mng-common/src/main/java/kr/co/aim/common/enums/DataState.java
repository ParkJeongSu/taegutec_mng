package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataState implements MetaDataEnum {
    CREATED("CREATED"),
    REMOVED("REMOVED");
    private final String value;
}
