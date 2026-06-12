package kr.co.aim.common.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StatTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * "20260610" -> 2026-06-10T00:00:00
     */
    public static LocalDateTime toStartDateTime(String statDate) {
        LocalDate date = LocalDate.parse(statDate, FORMATTER);
        return date.atStartOfDay(); // 00:00:00
    }

    /**
     * "20260610" -> 2026-06-10T23:59:59.999999999
     */
    public static LocalDateTime toEndDateTime(String statDate) {
        LocalDate date = LocalDate.parse(statDate, FORMATTER);
        return date.atTime(LocalTime.MAX); // 23:59:59.999999999
    }
}