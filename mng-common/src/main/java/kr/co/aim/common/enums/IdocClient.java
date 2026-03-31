package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocClient implements MetaDataEnum {
    GAL("001"),
    MNG("999");
    private final String value;
}
