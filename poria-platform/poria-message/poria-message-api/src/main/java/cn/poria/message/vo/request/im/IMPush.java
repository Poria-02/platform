package cn.poria.message.vo.request.im;

import cn.poria.message.enums.OfflinePushInfo;
import cn.poria.message.enums.im.type.IMMsgBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchunlei
 * @date 2021年12月29日 6:07 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMPush {

  @Schema(description = "推送账号集合")
  private List<String> to;

  @Schema(description = "消息内容 文本消息  图片 语音 视频 地理位置信息 文件 提示消息 自定义消息")
  private IMMsgBody msgBody;

  @Schema(description = "推送配置")
  private OfflinePushInfo pushInfo;

}
