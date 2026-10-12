package cn.poria.message.vo.request;

import cn.poria.message.enums.SmsSign;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class SmsSend {


    @NotBlank(message="key不能为空")
    private String key;

    /**
     * 参数列表
     */
    private Map<String,String> param;

    @NotBlank(message = "手机号不能为空")
    private String phones;

    private SmsSign sign;

    private Long orgId;

    private String accountId;

}
