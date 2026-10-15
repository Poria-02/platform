package cn.poria.message.controller.inner;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
import cn.poria.message.service.SmsServiceManager;
import cn.poria.message.vo.request.SmsSend;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务间短信发送与短链接转换控制层
 */
@Slf4j
@RestController
@RequestMapping("/inner/sms")
@Hidden
public class SmsInnerController {

    /**
     * 短信发送服务对象
     */
    @Autowired
    private SmsServiceManager smsServiceManager;

    /**
     * 短链接转换服务地址
     */
    @Value("${convert.url}")
    private String convertUrl;

    /**
     * 发送通用短信
     *
     * @param req 接收手机号、模板及短信参数
     * @return 发送结果
     */
    @Inner
    @PostMapping("/send")
    @Operation(summary = "发送通用短信", description = "发送通用短信")
    public R sendSms(@RequestBody @Validated SmsSend req) {
        smsServiceManager.sendSms(req);
        return R.ok();
    }

    /**
     * 将长链接转换为短链接
     *
     * @param longUrl 待转换的长链接
     * @return 转换后的短链接
     */
    @Inner
    @PostMapping("/convert/shortUrl")
    @Operation(summary = "将长链接转换为短链接", description = "将长链接转换为短链接")
    public R<String> convertToShortUrl(@RequestBody @NotBlank(message = "链接不能为空") String longUrl) {
        log.info("获取短链接URL：{}", longUrl);
        JSONObject params = new JSONObject();
        params.set("longURL", longUrl);
        String result = HttpUtil.post(convertUrl, JSONUtil.toJsonStr(params));
        log.info("转短链接返回：{}", result);
        JSONObject resultJson = JSONUtil.parseObj(result);
        Assert.isTrue(resultJson.getStr("code").equals("200"), "短链接转换错误");
        return R.ok(resultJson.getStr("data"));
    }
}
