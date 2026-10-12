package cn.poria.message.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.message.service.im.tx.util.TxIMMsgSendUtil;
import cn.poria.message.service.video.tx.config.TxTRTCProperties;
import cn.poria.message.service.video.tx.config.TxVodProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.vod.v20180717.VodClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TrtcConfiguration {


    private final static String TRTC_HOST = "trtc.tencentcloudapi.com";

    private final static String VOD_HOST = "vod.tencentcloudapi.com";

    @Autowired
    private TxTRTCProperties trtcProperties;

    @Autowired
    private TxVodProperties vodProperties;

    @Autowired
    private TxIMMsgSendUtil txIMMsgSendUtil;
    private String url = "https://callkit.trtc.tencent-cloud.com/v1/callback/set?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json";
    @PostConstruct
    public void init(){
        if(StrUtil.isNotBlank(trtcProperties.getCallbackUrl())){
            log.info("创建视频通话回调配置地址:{}", trtcProperties.getCallbackUrl());
            String url = String.format(this.url, trtcProperties.getSdkAppId(), trtcProperties.getAdminAccountName(), txIMMsgSendUtil.getSig(trtcProperties.getAdminAccountName()), RandomUtil.getRandom().nextInt());
            HttpRequest request = HttpUtil.createPost(url);
            JSONArray arrays = JSONUtil.createArray();
            arrays.add("cancel");
            arrays.add("reject");
            arrays.add("not_answer");
            arrays.add("normal_end");
            arrays.add("call_busy");
            arrays.add("interrupt");
            JSONObject param = JSONUtil.createObj().putOnce("address",trtcProperties.getCallbackUrl()).putOnce("actions",arrays);
            request.body(param.toString());
            HttpResponse response = request.execute();
            log.info("创建回调配置结果:{}", JSONUtil.toJsonStr(response.body()));
        }
    }


    @Bean
    public TrtcClient trtcClient (){
        Credential cred = new Credential(trtcProperties.getSecretId(), trtcProperties.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(TRTC_HOST);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new TrtcClient(cred, "ap-beijing", clientProfile);
    }


    @Bean
    public VodClient vodClient() {
        Credential cred = new Credential(trtcProperties.getSecretId(), trtcProperties.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(VOD_HOST);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new VodClient(cred, "", clientProfile);
    }

}
