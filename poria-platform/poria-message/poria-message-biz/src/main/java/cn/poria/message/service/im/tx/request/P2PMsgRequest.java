package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import cn.hutool.json.JSONArray;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class P2PMsgRequest {

    /**
     * 1：把消息同步到 From_Account 在线终端和漫游上；
     * 2：消息不同步至 From_Account；
     * 若不填写默认情况下会将消息存 From_Account 漫游
     */
    @Alias("syncOtherMachine")
    private Integer syncOtherMachine;

    /**
     * 消息发送方 UserID（用于指定发送消息方帐号）
     */
    @Alias("From_Account")
    private String fromAccount;

    /**]]
     * 消息接收方 UserID
     */
    @Alias("To_Account")
    private String toAccount;


    /**
     * 	消息离线保存时长（单位：秒），最长为7天（604800秒）
     * 若设置该字段为0，则消息只发在线用户，不保存离线
     * 若设置该字段超过7天（604800秒），仍只保存7天
     * 若不设置该字段，则默认保存7天
     */
    @Alias("MsgLifeTime")
    private Integer msgLifeTime;

    /**
     * 消息序列号（32位无符号整数），后台会根据该字段去重及进行同秒内消息的排序，详细规则请看本接口的功能说明。若不填该字段，则由后台填入随机数
     */
    @Alias("MsgSeq")
    private String msgSeq;

    /***]]
     * 消息随机数（32位无符号整数），后台用于同一秒内的消息去重。请确保该字段填的是随机
     */
    @Alias("MsgRandom")
    private long msgRandom;

    @Alias("MsgBody")
    private Object msgBody;

    @Alias("MsgType")
    private Integer msgType;

    @Alias("MsgContent")
    private Integer msgContent;

    /**
     * 消息自定义数据（云端保存，会发送到对端，程序卸载重装后还能拉取到）
     */
    @Alias("CloudCustomData")
    private String cloudCustomData;

    /**
     * 该条消息是否支持消息扩展，0为不支持，1为支持。
     */
    @Alias("SupportMessageExtension")
    private Integer supportMessageExtension;

    /**
     *离线推送信息配置
     */
    @Alias("OfflinePushInfo")
    private Object offlinePushInfo;

    /**
     *该条消息是否需要已读回执，0为不需要，1为需要，默认为0
     */
    @Alias("IsNeedReadReceipt")
    private Integer isNeedReadReceipt;
}
