package cn.poria.message.vo.request;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PendingTaskQueryModel extends PageSearch {

    @Schema(description = "状态 0-待办 1-已办")
    private Integer status;

    @Schema(description = "代办任务类型 99-站内信")
    private Integer type;

}
