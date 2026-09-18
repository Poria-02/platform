package cn.poria.base.vo.request.banner;

import cn.poria.common.core.validate.Create;
import cn.poria.common.core.validate.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerModel {

    @Schema(description = "主键")
    @NotBlank(message = "id不能为空",groups = Update.class)
    private String id;

    @NotBlank(message = "标题不能为空",groups = Create.class)
    @Schema(description = "标题长度200")
    private String title;

    @NotBlank(message = "code不能为空",groups = Create.class)
    @Schema(description = "code 长度20")
    private String code;

    @Schema(description = "描述 长度600")
    private String description;

    @Schema(description = "状态1-启用，2-停用")
    private Integer status;


}

