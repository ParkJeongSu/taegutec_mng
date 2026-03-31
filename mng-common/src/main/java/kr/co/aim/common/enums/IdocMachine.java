package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocMachine implements MetaDataEnum {
    GAL(20L),
    MNG(1L);
    private final Long value;
}
