package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CarrierUseState implements MetaDataEnum {
    // 사용 가능 Carrier 에 비어있는 상태
    AVAILABLE("Available"),
    // 사용이 불가능한 상태
    NOT_AVAILABLE("NotAvailable"),
    // 사용중인 상태
    IN_USE("InUse");
    private final String value;
}
