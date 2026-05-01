package kr.co.aim.common.Utils;

public class QueryDslUtils {

    /**
     * 잘못된 특수문자가 섞인 프로퍼티를 걸러냅니다.
     */
    public static boolean isValidProperty(String property) {
        if (property == null || property.isEmpty()) {
            return false;
        }

        // 스웨거 오류 등으로 인해 [, ], ", : 등이 섞여 들어오는 경우를 원천 차단
        String invalidChars = "[]\": ";
        for (int i = 0; i < invalidChars.length(); i++) {
            if (property.indexOf(invalidChars.charAt(i)) != -1) {
                return false;
            }
        }

        return true;
    }
}
