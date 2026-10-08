package cn.poria.upms.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "网关路由信息")
public class SysRouteConf extends Model<SysRouteConf> {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @TableId(type = IdType.AUTO)
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

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "删除标记,1:已删除,0:正常")
    private String delFlag;
}
