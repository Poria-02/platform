package cn.poria.base.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 版权所有 泰山信息技术有限公司
 *
 * @Author: yuhaitao
 * @Date 2020/5/27 9:30 下午
 */
@Schema(description = "多个对象存储访问请求")
@Data
public class AccessListRequest {
    @Schema(description = "对象名称，可以为全路径")
    private List<String> objNames;
}

