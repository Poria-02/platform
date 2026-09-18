package cn.poria.base.vo.request.banner;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class BannerItemModel {

    @Schema(description = "id")
    @NotBlank(message = "id不能为空",groups = Update.class)
    private String id;

    /**
     *bannerId
     */

    @NotBlank(message = "bannerId不能为空",groups = Create.class)
    @Schema(description = "bannerId")
    private String bannerId;

    @Schema(description = "广告项类型(字典)")
    private String bannerItemType;

    /**
     *标题 200
     */

    @Schema(description = "标题 200")
    @NotBlank(message = "标题不能为空",groups = Create.class)
    private String title;

    @Schema(description = "图片地址")
    private String imageUrl;

    /**
     *起始投放日期
     */

    @Schema(description = "起始投放日期")
    @NotNull(message = "起始投放日期不能为空",groups = Create.class)
    private Date beginDate;

    /**
     *截止投放日期
     */

    @Schema(description = "截止投放日期")
    @NotNull(message = "截止投放日期不能为空",groups = Create.class)
    private Date endDate;

    /**
     *内容 5000
     */

    @Schema(description = "内容 5000")
    private String content;

    /**
     *排序 2
     */

    @Schema(description = "排序 2")
    private Integer sort;

    /**
     *状态1-启用2-停用
     */

    @Schema(description = "状态1-启用2-停用")
    private Integer status;


    @Schema(description = "是否需要登录1-需要，2-不需要")
    private Integer needLogin;
}

