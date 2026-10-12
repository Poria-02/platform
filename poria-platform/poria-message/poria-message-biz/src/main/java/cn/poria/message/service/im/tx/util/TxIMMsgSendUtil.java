package cn.poria.message.service.im.tx.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.service.im.tx.config.TxIMProperties;
import cn.poria.message.enums.txim.TxMsgConstant;
import cn.poria.message.enums.txim.TxMsgUrlConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TxIMMsgSendUtil {

    @Lazy
    @Autowired
    private TxIMProperties txIMProperties;

    @Autowired
    private RedisTemplate redisTemplate;


    public JSONObject sendPostReq(String url, String identifier, String bodyJsonStr){
        url = TxMsgUrlConstant.MSG_HOST + TxMsgUrlConstant.MSG_VERSION + url +
                StrUtil.format(TxMsgUrlConstant.MSG_PARAM,txIMProperties.getSdkAppId(),
                        identifier,
                        this.getSig(identifier),
                        RandomUtil.randomNumbers(32)
                );
        HttpRequest request = HttpUtil.createPost(url);
        Map<String,String> requestHeader = new HashMap<>();
        request.headerMap(requestHeader,true).body(bodyJsonStr,"application/json; charset=utf-8");
        String txResponseBody = request.execute().body();
        log.info("【Tx request】url:{} \n body: {} \n response: {}",url, JSONUtil.toJsonStr(bodyJsonStr),txResponseBody);
        JSONObject result = JSONUtil.parseObj(txResponseBody);
        if(result.getInt("ErrorCode") != 0){
            log.info("【Tx Exception】url:{} \n body: {} \n response: {}",url,JSONUtil.toJsonStr(bodyJsonStr),result.toString());
            throw new ServiceException("腾讯IM请求异常，错误码" + result.getStr("ErrorCode"));
        }
        return result;
    }


    public String getSig(String userId){
        String sig = (String) redisTemplate.opsForValue().get(TxMsgConstant.SIG_CACHE_KEY + userId);
        if(StrUtil.isBlank(sig)){
            TLSSigAPIv2Util tlsSigAPIv2Util = new TLSSigAPIv2Util(txIMProperties.getSdkAppId(),txIMProperties.getSecretKey());
            sig = tlsSigAPIv2Util.genUserSig(userId, txIMProperties.getSingExpireTime());
            redisTemplate.opsForValue().set(TxMsgConstant.SIG_CACHE_KEY + userId,sig,txIMProperties.getSingExpireTime(), TimeUnit.SECONDS);
        }
        return sig;
    }


    public void deleteSig(List<String> userIds){
        redisTemplate.delete(userIds.stream().map(userId -> TxMsgConstant.SIG_CACHE_KEY + userId).collect(Collectors.toList()));
    }
}
