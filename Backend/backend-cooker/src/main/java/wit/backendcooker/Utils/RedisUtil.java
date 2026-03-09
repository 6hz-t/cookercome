package wit.backendcooker.Utils;

/*
 * @author ：jee
 * @date ：2026/3/816:55
 * @version: 1.0
 --------------------------
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {


    @Autowired
    private RedisTemplate redisTemplate;


    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
