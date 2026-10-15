package cn.poria.message.vo.request.center;

import cn.poria.common.core.dto.PageSearch;
import cn.poria.message.enums.AnnouncementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
     * 公告状态
     */
    @Schema(description = "公告状态：0-草稿，1-已发布，2-已撤回，默认查询已发布", allowableValues = {"0", "1", "2"}, defaultValue = "1")
    @NotNull
    @Min(0)
    @Max(2)
    private Integer status = AnnouncementStatus.PUBLISHED.getCode();

    /**
     * 查询关键字
     */
    @Schema(description = "查询标题或内容，最多100字", maxLength = 100)
    @Size(max = 100)
    private String keyword;

}
