package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeName("imMsgTypeFile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeFile implements IMMsgBody {

    @Override
    public String getMsgType() {
        return "TIMFileElem";
    }

    @Schema(description = "文件下载地址")
    @Alias("Url")
    private String url;

    @Schema(description = "文件的唯一标识")
    @Alias("UUID")
    private String uuid;

    @Schema(description = "文件数据大小 字节")
    @Alias("FileSize")
    private Long fileSize;

    @Schema(description = "文件名称")
    @Alias("FileName")
    private String fileName;

    @Schema(description = "图片下载地址 目前取值为2")
    @Alias("Download_Flag")
    private Integer downloadFlag =  2;


}
