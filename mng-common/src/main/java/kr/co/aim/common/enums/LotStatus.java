package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LotStatus implements MetaDataEnum {
    WIP("WIP"),
    STOCK("STOCK");
    private final String value;
}
