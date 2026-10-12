package cn.poria.message.vo.response.cs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author qiaodi
 * date 2025/9/16 11:32
 * version 6.7.3
 * description
 */
@Data
public class ContentSecurityVo {

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "对应的数据ID")
    private String dataId;

    @Schema(description = "检测的任务ID")
    private String taskId;

    @Schema(description = "文本、图片类型结果")
    private List<ContentSecurityResult> result;

    @Schema(description = "视频结果，map key:frameResult，audioResult")
    private Map<String,List<ContentSecurityResult>> videoResult;

    @Data
    public static class ContentSecurityResult{

        @Schema(description = "标签")
        private String label;

        @Schema(description = "描述")
        private String description;

        @Schema(description = "风险等级")
        private String riskLevel;
    }
}
