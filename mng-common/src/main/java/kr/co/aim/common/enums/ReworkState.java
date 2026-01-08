package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReworkState implements MetaDataEnum {
    REWORK("Rework"),
    NOT_IN_REWORK("NotInRework");
    private final String value;
}
