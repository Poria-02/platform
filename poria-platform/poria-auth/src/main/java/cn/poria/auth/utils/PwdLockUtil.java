package cn.poria.auth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PwdLockUtil {


    @Autowired
    private RedisTemplate redisTemplate;

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /***
     * 前置处理 是否锁定账号
     */
    public void preProcessing(String cacheKey) {
        //校验10分钟是否连续输错密码5次
        if (redisTemplate.hasKey(cacheKey) && "true".equals(redisTemplate.opsForValue().get(cacheKey)))
            throw new BadCredentialsException("用户名或密码错误");
    }


    /***
     * 后置处理 处理密码错误次数
     */
    public void postProcessing(String credentials, String password, String cacheKey) {

        //如果已被锁定 直接抛出异常
        if ("true".equals(redisTemplate.opsForValue().get(cacheKey))) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        //如果验证通过 则清楚错误次数
        if (ENCODER.matches(credentials, password)) {
            redisTemplate.delete(cacheKey);
            return;
        }

        //校验是否连续输错密码5次
        if (!redisTemplate.hasKey(cacheKey)) {
            //第一次
            redisTemplate.opsForValue().set(cacheKey, 1);
            throw new BadCredentialsException("用户名或密码错误");
        } else if (4 == (int) redisTemplate.opsForValue().get(cacheKey)) {
            //第5次
            redisTemplate.opsForValue().set(cacheKey, "true", 8, TimeUnit.HOURS);
            throw new BadCredentialsException("用户名或密码错误");
        } else {
            //多次
            int oldValue = (int) redisTemplate.opsForValue().get(cacheKey);
            redisTemplate.opsForValue().set(cacheKey, oldValue + 1);
            throw new BadCredentialsException("用户名或密码错误");
        }

    }


}
