package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@JsonTypeName("imMsgTypeVideo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IMMsgTypeVideo implements IMMsgBody {

    @Override
    public String getMsgType() {
        return "TIMVideoFileElem";
    }


    @Schema(description = "视频下载地址")
    @Alias("VideoUrl")
    private String videoUrl;

    @Schema(description = "视频的唯一标识")
    @Alias("VideoUUID")
    private String videoUUID;

    @Schema(description = "视频数据大小，单位：字节")
    @Alias("VideoSize")
    private Long videoSize;

    @Schema(description = "视频时长，单位：秒")
    @Alias("VideoSecond")
    private Long videoSecond;

    @Schema(description = "视频格式，例如 mp4")
    @Alias("VideoFormat")
    private String videoFormat;

    @Schema(description = "视频下载方式标记 目前只能为2")
    @Alias("VideoDownloadFlag")
    private Integer videoDownloadFlag = 2;

    @Schema(description = "视频缩略图下载地址")
    @Alias("ThumbUrl")
    private String thumbUrl;

    @Schema(description = "视频缩略图的唯一标识")
    @Alias("ThumbUUID")
    private String thumbUUID;

    @Schema(description = "缩略图大小 单位：字节")
    @Alias("ThumbSize")
    private Long thumbSize;

    @Schema(description = "缩略图宽度 单位 像素")
    @Alias("ThumbWidth")
    private Long thumbWidth;

    @Schema(description = "缩略图高度 单位 像素")
    @Alias("ThumbHeight")
    private Long thumbHeight;

    @Schema(description = "缩略图格式 例如 JPG、BMP 等")
    @Alias("ThumbFormat")
    private String thumbFormat;

    @Schema(description = "视频缩略图下载方式标记 目前只能为2")
    @Alias("ThumbDownloadFlag")
    private Integer thumbDownloadFlag = 2;


}
