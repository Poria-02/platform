package cn.poria.message.vo.request;

import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.vo.request.im.IMPush;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingTaskModel {

    /**
     *用户ID
     */

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;

    /**
     *关联用户ID
     */

    @Schema(description = "关联用户ID")
    private String relateUserId;

    /**
     *关联用户姓名
     */

    @Schema(description = "关联用户姓名")
    private String relateUserName;

    /**
     *关联用户头像
     */

    @Schema(description = "关联用户头像")
    private String relateUserHead;

    /**
     *业务ID
     */

    @Schema(description = "业务ID")
    private String businessId;


    @NotNull(message = "代办类型不能为空")
    @Schema(description = "代办任务类型")
    private PendingTaskType type;

    /**
     *任务跳转路径
     */

    @Schema(description = "任务跳转路径")
    private String url;

    /**
     *任务标题
     */
    @NotBlank(message = "标题不能为空")
    @Schema(description = "任务标题")
    private String title;

    /**
     *任务时间
     */

    @Schema(description = "任务时间")
    private String taskTime;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "0 不跳转 1 h5 2 原生")
    private Integer redirectType;

    @Schema(description = "im系统消息参数")
    private IMPush imPush;

}
