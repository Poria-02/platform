package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonTypeName("imMsgTypeVoice")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeVoice implements IMMsgBody {

    @Override
    public String getMsgType() {
        return "TIMSoundElem";
    }

    @Schema(description = "语音下载地址")
    @Alias("Url")
    private String url;

    @Schema(description = "语音的唯一标识")
    @Alias("UUID")
    private String uuid;

    @Schema(description = "语音数据大小 字节")
    @Alias("Size")
    private Long size;

    @Schema(description = "语音时长 秒")
    @Alias("Second")
    private Long second;

    @Schema(description = "语音下载方式标记 目前只能为2")
    @Alias("Download_Flag")
    private Integer downloadFlag = 2;


}
