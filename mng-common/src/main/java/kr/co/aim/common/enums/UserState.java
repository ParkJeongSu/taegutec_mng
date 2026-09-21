package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState implements MetaDataEnum {
    // 사용 가능
    ACTIVE("ACTIVE"),
    // 사용이 불가능한 상태
    LOCKED("LOCKED"),
    // 퇴직
    RETIRED("RETIRED");
    private final String value;
}
