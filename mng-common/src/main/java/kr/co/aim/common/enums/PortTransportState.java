package kr.co.aim.common.enums;

import kr.co.aim.common.handler.MetaDataEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortTransportState implements MetaDataEnum {
    READY_TO_LOAD("READY_TO_LOAD"){
        @Override
        public boolean canTransitionTo(PortTransportState nextState) {
            // RUN 상태에서는 IDLE 또는 DOWN으로만 변경 가능
            //return nextState == ReservedToLoad || nextState == ReadyToProcess;
            // 어떤 전이 규칙을 이곳에 작성 그래서 메모리로 관리
            return true;
        }
    },
    RESERVED_TO_LOAD("RESERVED_TO_LOAD"){

    },
    READY_TO_PROCESS("READY_TO_PROCESS"){

    },
    PROCESSING("PROCESSING"){

    },
    READY_TO_UNLOAD("READY_TO_UNLOAD"){

    },
    RESERVED_TO_UNLOAD("RESERVED_TO_UNLOAD"){

    };
    private final String value;



    public boolean canTransitionTo(PortTransportState nextState) {
        return true;
    }
}
