package cn.poria.message.vo.request.im;

import cn.poria.message.enums.OfflinePushInfo;
import cn.poria.message.enums.im.IMMsgOpe;
import cn.poria.message.enums.im.type.IMMsgBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchunlei
 * @date 2021年12月29日 4:36 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgSend {

    @Schema(description = "发送方账号id")
    private String from;

    @Schema(description = "接收方账号id")
    private String to;

    @Schema(description = "0：P2P消息  1：群消息")
    private IMMsgOpe ope;

    @Schema(description = "消息内容 文本消息  图片 语音 视频 地理位置信息 文件 提示消息 自定义消息")
    private IMMsgBody body;

    @Schema(description = "离线推送内容")
    private OfflinePushInfo pushInfo;

    @Schema(description = "群消息指定能收到消息的群成员id")
    private List<String> receiveMsgAccountIds;

    @Schema(description = "消息自定义数据")
    private String cloudCustomData;
}
