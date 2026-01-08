package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunicationState implements MetaDataEnum {
    OFFLINE("Offline"),
    ONLINE_LOCAL("OnlineLocal"),
    ONLINE_REMOTE("OnlineRemote");

    public static boolean isExist(String communicationState) {
        for (CommunicationState state : CommunicationState.values()) {
            if (state.name().equalsIgnoreCase(communicationState)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }
    private final String value;
}
