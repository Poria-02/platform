package cn.poria.auth.service;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.constant.enums.LoginTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * @author zhangchunlei
 * @date 2021年06月25日 1:51 下午
 */
@Service
@Slf4j
public class VerifyCodeService {

	@Autowired
	private StringRedisTemplate redisTemplate;


	public void  checkVerifyCode(String randomStr,String code){

		//校验验证码是否正确
		String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		if (!redisTemplate.hasKey(key)) {
			throw new BadCredentialsException("验证码不合法");
		}
		Object codeObj = redisTemplate.opsForValue().get(key);
		if (codeObj == null) {
			throw new BadCredentialsException("验证码不合法");
		}
		String saveCode = codeObj.toString();
		if (StrUtil.isBlank(saveCode)) {
			redisTemplate.delete(key);
			throw new BadCredentialsException("验证码不合法");
		}

		//验证码输入错误
		if (!StrUtil.equals(saveCode, code)) {
			throw new BadCredentialsException("验证码不合法");
		}

		redisTemplate.delete(key);
	}

	public void CheckStaffSmsVerifyCode(String mobile,String type ,String code){
		String redisKey = CacheConstants.DEFAULT_CODE_KEY + LoginTypeEnum.SMS.getType() + StringPool.AT + type + StringPool.PIPE + mobile;
		//DEFAULT_CODE_KEY:SMS@1005|17710026695
		Object cacheCode = redisTemplate.opsForValue().get(redisKey);
		log.info("登录验证码 key: {}  value:  {}", redisKey, null == cacheCode?null:cacheCode.toString());
		if(cacheCode == null ){
			throw new BadCredentialsException("请先获取验证码");
		}
		if(!StrUtil.equals(code,cacheCode.toString())){
			throw new BadCredentialsException("验证码错误");
		}

		redisTemplate.delete(redisKey);
	}


	public void checkAdminSmsVerifyCode(String mobile, String type ,String code){
		String redisKey = CacheConstants.DEFAULT_CODE_KEY + LoginTypeEnum.SMS.getType() + StringPool.AT + type + StringPool.PIPE + mobile;
		//DEFAULT_CODE_KEY:SMS@admin|17710026695
		Object cacheCode = redisTemplate.opsForValue().get(redisKey);
		log.info("登录验证码 key: {}  value:  {}", redisKey, null == cacheCode?null:cacheCode.toString());
		if(cacheCode == null ){
			throw new BadCredentialsException("请先获取验证码");
		}
		if(!StrUtil.equals(code,cacheCode.toString())){
			throw new BadCredentialsException("验证码错误");
		}

		redisTemplate.delete(redisKey);
	}
}
