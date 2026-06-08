package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReworkState implements MetaDataEnum {
    REWORK("REWORK"),
    NOT_IN_REWORK("NOT_IN_REWORK");
    private final String value;
}
