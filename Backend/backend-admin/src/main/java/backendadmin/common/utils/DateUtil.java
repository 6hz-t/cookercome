package backendadmin.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期时间工具类
 */
public class DateUtil {

    // 常用格式
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 获取当前日期字符串（yyyy-MM-dd）
     */
    public static String getCurrentDateStr() {
        return LocalDate.now().format(YYYY_MM_DD);
    }

    /**
     * 获取当前时间字符串（yyyy-MM-dd HH:mm:ss）
     */
    public static String getCurrentDateTimeStr() {
        return LocalDateTime.now().format(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 日期字符串转 LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, YYYY_MM_DD);
    }

    /**
     * 时间字符串转 LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当月第一天
     */
    public static LocalDate getFirstDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取当月最后一天
     */
    public static LocalDate getLastDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }
}