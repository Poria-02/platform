package cn.poria.message.vo.request.center;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公示公告记录分页查询请求
 */
@Data
@Schema(description = "公示公告记录分页查询请求")
@EqualsAndHashCode(callSuper = true)
public class CenterQueryModel extends PageSearch {

    /**
     * 查询关键字
     */
    @Schema(description = "查询标题或内容，最多100字", maxLength = 100)
    @Size(max = 100)
    private String keyword;

    /**
     * 公告发布状态
     */
    @Schema(description = "公告发布状态：0-草稿，1-已发布，2-已撤回",
            allowableValues = {"0", "1", "2"})
    @Min(0)
    @Max(2)
    private Integer status;

}
