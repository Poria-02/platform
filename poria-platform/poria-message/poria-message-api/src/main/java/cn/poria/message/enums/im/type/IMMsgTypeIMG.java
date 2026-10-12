package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonTypeName("imMsgTypeIMG")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeIMG implements IMMsgBody {

    @Override
    public String getMsgType() {
        return "TIMImageElem";
    }

    @Schema(description = "唯一标识id")
    @Alias("UUID")
    private String uuid;

    @Schema(description = "图片格式 1-JPG，2-GIF，3-PNG，4-BMP，255-其他")
    @Alias("ImageFormat")
    private String imageFormat;

    @Schema(description = "原图、缩略图或者大图下载信息")
    @Alias("ImageInfoArray")
    private List<ImgInfo> imageInfoArray;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImgInfo {

        @Schema(description = "图片类型 1-原图，2-大图，3-缩略图")
        @Alias("Type")
        private Integer type;

        @Schema(description = "图片数据大小  字节")
        @Alias("Size")
        private Long size;

        @Schema(description = "图片宽度  像素")
        @Alias("Width")
        private Long width;

        @Schema(description = "图片高度  像素")
        @Alias("Height")
        private Long height;

        @Schema(description = "图片下载地址")
        @Alias("URL")
        private String url;
    }

}
