package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IfEventQueueState implements MetaDataEnum {
    READY("READY"),
    PROCESSING("PROCESSING"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL");
    private final String value;
}
