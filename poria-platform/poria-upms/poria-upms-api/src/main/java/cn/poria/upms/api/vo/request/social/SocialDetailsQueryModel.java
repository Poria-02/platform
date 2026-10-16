package cn.poria.upms.api.vo.request.social;

import cn.poria.common.core.dto.PageSearch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 第三方账号分页及查询条件
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SocialDetailsQueryModel extends PageSearch {

    @Schema(description = "账号类型，WX：公众号，MINI：微信小程序，ALI_MA：支付宝小程序")
    private String type;

    @Schema(description = "账号类型列表，用于组合查询公众号与微信小程序")
    private List<String> types;

    @Schema(description = "AppId，精确匹配")
    private String appId;

    @Schema(description = "账号描述关键字")
    private String keywords;
}
