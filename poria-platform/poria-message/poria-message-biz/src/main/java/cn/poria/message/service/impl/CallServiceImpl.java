package cn.poria.message.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.message.config.CallProperties;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.service.CallService;
import cn.poria.message.util.TemplateParamUtil;
import cn.poria.message.vo.request.outbound.OutBoundModel;
import cn.poria.message.vo.response.template.TemplateVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CallServiceImpl implements CallService {

    @Autowired
    private CallProperties callProperties;

    @Autowired
    private MsgTemplateService msgTemplateService;

    @Override
    public void outBound(OutBoundModel model) {

        TemplateVo templateVo = msgTemplateService.selectByKey(model.getTemplateKey());
        Assert.notNull(templateVo,"模板不存在");
        if(templateVo.getStatus() == 2) return;
        if(!StrUtil.equals(templateVo.getType(),"10") ) return;
        long timestamp = System.currentTimeMillis();

        Map<String, String> param = TemplateParamUtil.settingTemplateParam(templateVo, model.getParams());
        model.setParams(param);
        JSONObject params = JSONUtil.createObj()
                .set("speechId",templateVo.getContent())
                .set("planName",model.getPlanName())
                .set("dwellers",JSONUtil.createArray().set(JSONUtil.createObj()
                        .set("dwellerName",model.getPatientName())
                        .set("telephone",model.getMobile())
                        .set("relationId",model.getBusinessId())))
                .set("type",1)
                .set("vars",model.getParams().entrySet().stream().map(entry->
                        JSONUtil.createObj().set("remarks",entry.getKey()).set("content",entry.getValue())).collect(Collectors.toList()));
        String url = callProperties.getUrl() +
                String.format("speech-api/v1/pb/openapi/call-task?appId=%s&timestamp=%s&sign=%s",
                        callProperties.getAppId(),timestamp,generateSign(timestamp));
        log.info("url:{}",url);
        log.info("body:{}",params.toString());
        HttpRequest request = HttpUtil.createPost(url).body(params.toString());
        HttpResponse response = request.execute();
        log.info("response:{}",response.body());
    }

    @Override
    public Object boundResult(String businessId) {
        JSONObject json = JSONUtil.createObj().set("relationId",businessId);
        long timestamp = System.currentTimeMillis();
        String url = callProperties.getUrl() +
                String.format("report-api/v2/pb/openapi/record-detail?appId=%s&timestamp=%s&sign=%s",
                        callProperties.getAppId(),timestamp,generateSign(timestamp));
        HttpRequest request = HttpUtil.createPost(url);
        request.body(json.toString());
        log.info("请求:{}",json.toString());
        HttpResponse response = request.execute();
        log.info("response:{}",JSONUtil.toJsonStr( response.body()));
        return  response.body();
    }


    @SneakyThrows
    @Override
    public void callResult(String request) {
        //aes 解密
        byte[] content = parseHexStr2Byte(request);
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(callProperties.getSecretKey().getBytes());
        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(content);
        // 加密
        String s =  new String(result);

        log.info("返回结果:{}",s);
    }


    /**
     * 将十六进制转换为二进制
     *
     * @param hexStr 内容
     * @return 二进制值
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    private String generateSign(long timestamp){
        return DigestUtil.md5Hex(callProperties.getAppId()+ timestamp + callProperties.getSecretKey());
    }

}
