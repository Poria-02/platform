package cn.poria.message.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class LogQuery {

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date beginDate;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date endDate;

    @Schema(description = "每页条数")
    private Integer size = 10;

    @Schema(description = "当前页")
    private Integer current = 1;

}
