package cn.poria.message.service.video.tx.util;

import cn.hutool.core.util.ObjectUtil;
import cn.poria.message.service.video.tx.bean.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisIMVideoSupportUtil {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    public  void  putMeetingMap(String key, String hashKey, Room o) {
        redisTemplate.execute((RedisCallback<Object>) conn -> {
            conn.hSet(key.getBytes(),hashKey.getBytes(), ObjectUtil.serialize(o));
            return null;
        });
    }


    public  Object  getRoom(String key, String hashKey) {
        return redisTemplate.execute((RedisCallback<Object>) conn -> {
            byte[] hGet = conn.hGet(key.getBytes(), hashKey.getBytes());
            return hGet == null ? null : ObjectUtil.deserialize(hGet);
        });
    }


    public  void  delMap(String key,String hashKey) {
        redisTemplate.execute((RedisCallback<Object>) conn -> {
            conn.hDel(key.getBytes(), hashKey.getBytes());
            return null;
        });
    }


}
