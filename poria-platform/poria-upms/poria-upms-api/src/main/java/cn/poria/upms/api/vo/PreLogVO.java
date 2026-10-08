package cn.poria.upms.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "前端日志展示对象")
public class PreLogVO {

    @Schema(description = "请求url")
    private String url;

    @Schema(description = "请求耗时")
    private String time;

    @Schema(description = "请求用户")
    private String user;

    @Schema(description = "请求结果0:成功9:失败")
    private String type;

    @Schema(description = "请求传递参数")
    private String message;

    @Schema(description = "异常信息")
    private String stack;

    @Schema(description = "日志标题")
    private String info;
}
