
package cn.poria.base.controller.api;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.util.ParamResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 重建登录控制器
 *
 * @author makejava
 * @since 2024-01-01
 */
@RestController
@Tag(name = "rebuild接口")
@RequestMapping("/api/rebuild")
@Validated
public class RebuildLoginController {


    @Value("${rebuild.appid}")
    private String appId;
    @Value("${rebuild.appSecret}")
    private String appSecret;
    @Value("${rebuild.url}")
    private String url;

    @Value("${rebuild.user}")
    private String user;

    @Value("${rebuild.password}")
    private String password;

    /**
     * 重建登录
     */
    @Operation(summary = "生成签名")
    @PostMapping("/sgin")
    public R<String> sgin() {
        String sign = sign(null,"MD5");
        HttpRequest request = HttpRequest.get(this.url + "/gw/api/login-token?" + sign);
        HttpResponse execute = request.execute();
        return R.ok(execute.body());
    }


    

    /**
     * 签名（MD5）
     *
     * @param reqParams
     * @return
     * @see #sign(Map, String)
     */
    public String sign(Map<String, Object> reqParams) {
        return sign(reqParams, "MD5");
    }

    /**
     * 签名
     *
     * @param reqParams
     * @param signType
     * @return
     */
    public String sign(Map<String, Object> reqParams, String signType) {
        Map<String, Object> sortMap = new TreeMap<>();
        if (reqParams != null && !reqParams.isEmpty()) {
            sortMap.putAll(reqParams);
        }
        sortMap.put("user", this.user);
        sortMap.put("password", this.password);
        sortMap.put("appid", this.appId);
        sortMap.put("timestamp", System.currentTimeMillis() / 1000);  // in sec
        sortMap.put("sign_type", signType);

        StringBuilder sign = new StringBuilder();
        for (Map.Entry<String, Object> e : sortMap.entrySet()) {
            sign.append(e.getKey())
                    .append('=')
                    .append(e.getValue())
                    .append('&');
        }

        final String signUrl = sign + "sign=";

        // 拼接
        sign.append(this.appId)
                .append('.')
                .append(this.appSecret);

        if ("MD5".equals(signType)) {
            return signUrl + MD5.create().digestHex(sign.toString());
        } else {
            throw new IllegalArgumentException("signType=" + signType);
        }
    }

}

