package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentState implements MetaDataEnum {
    RUN("RUN"){
        @Override
        public boolean canTransitionTo(EquipmentState nextState) {
            // RUN 상태에서는 IDLE 또는 DOWN으로만 변경 가능
            //return nextState == IDLE || nextState == DOWN;
            // 어떤 전이 규칙을 이곳에 작성 그래서 메모리로 관리
            return true;
        }
    },
    IDLE("IDLE"){

    },
    STOP("STOP"){

    },
    DOWN("DOWN"){

    },
    PM("PM"){

    };
    private final String value;

    public boolean canTransitionTo(EquipmentState nextState) {
        return true;
    }

    public static boolean isExist(String equipmentState) {
        for (EquipmentState state : EquipmentState.values()) {
            if (state.name().equalsIgnoreCase(equipmentState)) {
                return true;
            }
        }
        // 일치하는 상수가 없으면 예외를 발생시키거나 null을 반환할 수 있습니다.
        return false;
    }
}
