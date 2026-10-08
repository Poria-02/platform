package cn.poria.common.data.conver.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 版权所有 泰山信息技术有限公司
 *
 * @Author: yuhaitao
 * @Date 2020/5/10 9:35 下午
 */
@Data
public class TreeDictItemVo implements Serializable {

    private String id;
    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;
    private String simpleName="";
    private String remark="";
    private String value="";
    private String ext1="";
    private String pid="";
	private Integer sort;
    private List<TreeDictItemVo> childs;

    @Schema(description = "字典ID")
    @NotBlank(message = "字典ID不能为空")
    private String dictId;
}
