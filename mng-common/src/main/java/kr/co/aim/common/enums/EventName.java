package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventName implements MetaDataEnum {
    CREATED("CREATED"),
    REMOVED("REMOVED"),
    UPDATED("UPDATED");

    private final String value;
}
