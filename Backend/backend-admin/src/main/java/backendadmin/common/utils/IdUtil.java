package backendadmin.common.utils;

import java.util.UUID;

/**
 * ID生成工具类
 */
public class IdUtil {

    /**
     * 生成32位UUID（无横杠）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成订单号（规则：前缀 + 年月日时分秒 + 6位随机数）
     * 例：CS20260309123456789012
     */
    public static String generateOrderNo(String prefix) {
        String timeStr = DateUtil.getCurrentDateTimeStr()
                .replace("-", "")
                .replace(" ", "")
                .replace(":", "");
        int random = (int) (Math.random() * 1000000);
        return prefix + timeStr + String.format("%06d", random);
    }

    /**
     * 生成默认前缀的订单号
     */
    public static String generateOrderNo() {
        return generateOrderNo("CS");
    }
}
