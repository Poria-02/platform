package cn.poria.message.vo.response.center;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 公示公告列表及详情响应
 */
@Data
@Schema(description = "公示公告记录列表及详情响应，阅读状态由待办任务维护")
public class AnnouncementVo {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID")
    private String id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题")
    private String title;

    /**
     * 公告正文
     */
    @Schema(description = "公告正文")
    private String content;

    /**
     * 公告发布状态
     */
    @Schema(description = "公告状态：0-草稿，1-已发布，2-已撤回", allowableValues = {"0", "1", "2"})
    private Integer status;

    /**
     * 发布人名称
     */
    @Schema(description = "发布人名称")
    private String publisher;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间，草稿及已撤回公告为空")
    private Date publishTime;

}
