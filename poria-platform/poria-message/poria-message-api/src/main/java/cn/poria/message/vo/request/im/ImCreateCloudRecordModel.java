package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 云端录制请求参数
 */
@Data
public class ImCreateCloudRecordModel {

    private String roomId;

    /**
     * 显示大屏的用户id
     */
    private String maxResolutionUserId;

    @Schema(description = "悬浮布局：1（不传默认为1）; 九宫格布局：3")
    private Integer type;

}
