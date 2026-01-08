package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmLevel implements MetaDataEnum {
    WARNING("Warning"),
    ERROR("Error");
    private final String value;
}
