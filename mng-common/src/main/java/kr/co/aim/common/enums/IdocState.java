package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdocState implements MetaDataEnum {
    INITIAL(10L),
    IN_PROCESS(15L),
    COMPLETED(20L);
    private final Long value;
}
