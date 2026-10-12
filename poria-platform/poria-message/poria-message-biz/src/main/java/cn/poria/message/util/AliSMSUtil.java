package cn.poria.message.util;
import  com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;

public class AliSMSUtil {



  /**
   * 使用AK&SK初始化账号Client
   * @param accessKeyId
   * @param accessKeySecret
   * @return Client
   * @throws Exception
   */
  public static  Client createClient(String accessKeyId,
      String accessKeySecret) throws Exception {
    Config config = new Config()
        // 您的AccessKey ID
        .setAccessKeyId(accessKeyId)
        // 您的AccessKey Secret
        .setAccessKeySecret(accessKeySecret);
    // 访问的域名
    config.endpoint = "dysmsapi.aliyuncs.com";
    return new Client(config);
  }

  /**
   *  aliyun SMS  返回值 SendSmsResponse 转String
   * @param sendSmsResponse
   * @return String
   */
  public static  String responseToString(SendSmsResponseBody sendSmsResponse) {

    return  "{ bizId :"+sendSmsResponse.getBizId() +",code:"+sendSmsResponse.getCode()
        +",message:"+sendSmsResponse.getMessage()+",requestId:"+sendSmsResponse.getRequestId()+" }";
  }

}
