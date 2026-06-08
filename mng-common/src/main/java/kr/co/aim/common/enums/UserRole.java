package kr.co.aim.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("ROLE_USER", "일반 사용자"){
        @Override
        public boolean canTransitionTo(EquipmentState nextState) {
            return false;
        }
    },
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String description;

    public boolean canTransitionTo(EquipmentState nextState) {
        return true;
    }
}
