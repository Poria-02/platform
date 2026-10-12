package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class P2PMsgRecordRequest {

    /**]]
     * 会话其中一方的 UserID
     */
    @Alias("Operator_Account")
    private String from;

    /**]]
     * 会话的另一方 UserID
     */
    @Alias("Peer_Account")
    private String to;

    /**]]
     * 	请求的消息条数
     */
    @Alias("MaxCnt")
    private Integer msgCount;

    /**]]
     * 	请求的消息时间范围的最小值
     */
    @Alias("MinTime")
    private Long startTime;

    /**]]
     * 请求的消息时间范围的最大值
     */
    @Alias("MaxTime")
    private Long endTime;

    /**
     * 上一次拉取到的最后一条消息的 MsgKey
     */
    @Alias("LastMsgKey")
    private String lastMsgKey;

}
