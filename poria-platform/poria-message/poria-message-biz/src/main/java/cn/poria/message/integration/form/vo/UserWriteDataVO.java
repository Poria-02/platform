package cn.poria.message.integration.form.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserWriteDataVO {


    private Long id;

    //提交序列号
    private Long serialNumber;

    private String  formKey;

   //完成时间
    private Long completeTime;

   //提交人微信信息
    private String wxUserInfo;

   //提交参数
    public String originalData;

   //提交人姓名
    private String createByName;

    private String createById;

   //最后修改人姓名
    private String updateByName;

    private String updateById;

   //创建时间
    private Date createTime;

   //更新时间
    private Date updateTime;

   //提交人环境信息
    private SubmitDataEnv submitEnvData;

    @Data
    public static class SubmitDataEnv{
        private String submitRequestIp;
        private String submitUa;
        private String submitOs;
        private String submitAddress;
        private String submitBrowser;
    }
}
