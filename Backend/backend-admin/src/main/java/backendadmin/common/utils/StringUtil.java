package backendadmin.common.utils;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空（null或空串）
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 手机号脱敏（中间4位替换为****）
     * 例：13812345678 → 138****5678
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 截取字符串（超出长度加...）
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}