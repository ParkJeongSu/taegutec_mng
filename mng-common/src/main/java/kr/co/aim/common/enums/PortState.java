package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortState implements MetaDataEnum {
    UP("UP"),
    DOWN("DOWN");
    private final String value;

    public static boolean isExist(String portStateName) {
        for (PortState state : PortState.values()) {
            if (state.name().equalsIgnoreCase(portStateName)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }
    public static boolean isNotExist(String portStateName) {
        return !isExist(portStateName);
    }

}
