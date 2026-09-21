package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UseState implements MetaDataEnum {
    // 사용 가능
    USE("USE"),
    // 사용이 불가능한 상태
    UNUSE("UNUSE");
    private final String value;
}
