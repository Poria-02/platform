package cn.poria.message.vo.request;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 当前用户待办任务查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PendingTaskQueryModel extends PageSearch {

    /**
     * 完成状态
     */
    @Schema(description = "状态：0-待办/未读，1-已办/已读，不传则查询全部状态", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 待办任务类型
     */
    @Schema(description = "待办任务类型：98-公示公告，99-站内信，不传则查询全部类型")
    private Integer type;

    /**
     * 查询关键字
     */
    @Schema(description = "查询标题或内容", maxLength = 20)
    @Size(max = 20)
    private String keyword;

}
