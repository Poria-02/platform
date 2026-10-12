package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2021年12月29日 5:35 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogModel {

    @Schema(description = "发送方账号id")
    private String from;

    @Schema(description = "接收方账号id")
    private String to;

    @Schema(description = "开始时间")
    private Long beginTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "数据条数")
    private Integer limit;

}
