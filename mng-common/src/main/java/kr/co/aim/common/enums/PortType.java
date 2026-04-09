package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortType implements MetaDataEnum {
    INPUT("Input"),
    OUTPUT("Output"),
    BOTH("Both");
    private final String value;

    public static boolean isExist(String portTypeName) {
        for (PortType type : PortType.values()) {
            if (type.name().equalsIgnoreCase(portTypeName)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }
}
