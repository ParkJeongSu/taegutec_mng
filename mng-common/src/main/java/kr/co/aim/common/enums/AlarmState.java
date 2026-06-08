package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AlarmState implements MetaDataEnum {
    SET("SET"),
    CLEAR("CLEAR");

    // 1. 최종적으로 사용할 불변 Map을 선언합니다.
    private static final Map<String, AlarmState> VALUE_MAP;

    // 2. static 초기화 블록을 사용해 Map의 내용을 채웁니다.
    static {
        // 임시로 사용할 변경 가능한 Map을 생성합니다.
        Map<String, AlarmState> map = new HashMap<>();

        // values() 배열을 순회하면서 Map에 값을 추가합니다.
        for (AlarmState state : values()) {
            map.put(state.getValue(), state);
        }

        // 생성된 Map을 변경할 수 없도록 만든 후 최종 Map에 할당합니다.
        VALUE_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<AlarmState> fromValue(String value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }

    // 대소문자를 구분하지 않고 문자열로부터 Enum 상수를 찾는 메소드
    public static boolean isExist(String alarmStateName) {
        for (AlarmState state : AlarmState.values()) {
            if (state.name().equalsIgnoreCase(alarmStateName)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }

    private final String value;
}
