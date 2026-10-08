package cn.poria.upms.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class RouteVo {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "路由id")
    private String routeId;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "断言")
    private String predicates;

    @Schema(description = "过滤器")
    private String filters;

    @Schema(description = "请求uri")
    private String uri;

    @TableField("`order`")
    @Schema(description = "排序值")
    private Integer order;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "元数据")
    private String metadata;
}
