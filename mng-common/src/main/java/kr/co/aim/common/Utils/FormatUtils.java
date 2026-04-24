package kr.co.aim.common.Utils;

import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    // 17자리 혹은 18자리 포맷 (요청하신 소수점 4자리까지 고려 시 SSSS 사용)
    // yyyy(4)MM(2)dd(2)HH(2)mm(2)ss(2)SSS(3) = 17자리
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 현재 시간 기반의 Transaction ID 생성
     */
    public static String generateTransactionId() {
        return LocalDateTime.now().format(ID_FORMATTER);
    }

    /**
     * 특정 LocalDateTime을 시스템 표준 포맷 문자열로 변환
     */
    public static String getTransactionId(LocalDateTime dateTime) {
        if (ObjectUtils.isEmpty(dateTime)) {
            return generateTransactionId();
        }
        return dateTime.format(ID_FORMATTER);
    }
}
