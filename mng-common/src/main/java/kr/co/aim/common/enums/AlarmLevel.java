package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmLevel implements MetaDataEnum {
    WARNING("WARNING"),
    ERROR("ERROR");
    private final String value;
}
