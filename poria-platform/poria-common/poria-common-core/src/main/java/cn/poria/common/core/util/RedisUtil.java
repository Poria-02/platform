package cn.poria.common.core.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

public class RedisUtil {

    public static RedisTemplate redisTemplate = SpringContextHolder.getBean(RedisTemplate.class);

    public static void pushHash(String key, String field, Object value){
        redisTemplate.opsForHash().put(key,field,value);

    }

    public static void removeHash(String key, String field){
        redisTemplate.opsForHash().delete(key,field);
    }

    public static Collection hashSet(String key){
        return redisTemplate.opsForHash().entries(key).values();
    }

}
