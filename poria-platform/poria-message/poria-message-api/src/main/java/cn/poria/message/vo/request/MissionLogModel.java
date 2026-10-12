package cn.poria.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MissionLogModel {

    @Schema(description = "发送者")
    private String fromUser;

    @Schema(description = "接收者")
    private String toUser;

    @Schema(description = "类型  1--文本 2--健康宣教")
    private Integer type;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "服务流程来源  1--筛查  2--慢病")
    private Integer form;

    @Schema(description = "业务id")
    private String businessId;
}
