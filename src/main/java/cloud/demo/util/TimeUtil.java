package cloud.demo.util;

import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * @program: comehome
 * @description: 关于时间的工具类
 * @author: ErFeng_V
 * @create: 2021-05-30 19:59
 */
public class TimeUtil {
    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String timeToString(Long time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }
}
