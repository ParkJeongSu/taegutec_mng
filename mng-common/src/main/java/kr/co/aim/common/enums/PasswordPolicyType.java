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
public enum PasswordPolicyType implements MetaDataEnum {

    MIN_CHARACTERS("MIN_CHARACTERS", "8", "Minimum Number Of Characters"),
    MIN_UPPER_CASE("MIN_UPPER_CASE", "1", "Minimum Number Of Upper Case Alphabetic Characters (A-Z)"),
    MIN_LOWER_CASE("MIN_LOWER_CASE", "1", "Minimum Number Of Lower Case Alphabetic Characters (a-z)"),
    MIN_NUMERIC("MIN_NUMERIC", "1", "Minimum Number Of Numeric Characters (0-9)"),
    MIN_SPECIAL_CHARACTERS("MIN_SPECIAL_CHARACTERS", "0", "Minimum Number Of Special Characters (!@#$%^&*()_+-=[]{}|;':\"<>?,./)"),
    MAX_IDENTICAL_CONSECUTIVE("MAX_IDENTICAL_CONSECUTIVE", "2", "Maximum Number Of Identical Consecutive Characters"),
    MAX_FAILED_LOGINS("MAX_FAILED_LOGINS", "3", "Maximum Number Of Failed Logins Before Password Is Locked"),
    MAX_DAYS_BEFORE_CHANGE("MAX_DAYS_BEFORE_CHANGE", "365", "Maximum Number Of Days Before Password Must Be Changed"),
    MIN_DAYS_BEFORE_CHANGE("MIN_DAYS_BEFORE_CHANGE", "Unlimited", "Minimum Number Of Days Before Password Can Be Changed"),
    PASSWORD_HISTORY_LIMIT("PASSWORD_HISTORY_LIMIT", "3", "Number Of Password Changes Before An Old Password Can Be Used Again");

    private final String value;
    private final String defaultValue;
    private final String description;

    private static final Map<String, PasswordPolicyType> VALUE_MAP;

    static {
        Map<String, PasswordPolicyType> map = new HashMap<>();
        for (PasswordPolicyType type : values()) {
            map.put(type.getValue(), type);
        }
        VALUE_MAP = Collections.unmodifiableMap(map);
    }

    public static Optional<PasswordPolicyType> fromValue(String value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }

    public static boolean isExist(String policyTypeName) {
        for (PasswordPolicyType type : PasswordPolicyType.values()) {
            if (type.name().equalsIgnoreCase(policyTypeName)) {
                return true;
            }
        }
        return false;
    }
}
