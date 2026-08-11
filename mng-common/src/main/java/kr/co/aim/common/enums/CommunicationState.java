package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunicationState implements MetaDataEnum {
    OFFLINE("OFFLINE"),
    ONLINE_LOCAL("ONLINE_LOCAL"),
    ONLINE_REMOTE("ONLINE_REMOTE");

    public static boolean isExist(String communicationState) {
        for (CommunicationState state : CommunicationState.values()) {
            if (state.name().equalsIgnoreCase(communicationState)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }

    public static boolean isNotExist(String communicationState) {
        return !isExist(communicationState);
    }

    private final String value;
}
