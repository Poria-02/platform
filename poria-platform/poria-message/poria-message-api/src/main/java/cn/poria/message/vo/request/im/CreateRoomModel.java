package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * author qd
 * date 2024/12/10 17:51
 * description 视频
 */
@Data
public class CreateRoomModel {

    @Schema(description = "房间id")
    @NotBlank(message = "房间id不能为空")
    private String roomId;

    @Schema(description = "房间名称")
    private String roomName;

    @Schema(description = "房间类型：默认会议房间Conference")
    private String roomType;

    @Schema(description = "预定会议开始时间，默认为当前时间")
    private Integer scheduleStartTime;

    @Schema(description = "预定会议结束时间，默认为开始时间后1小时，会议最小时间不得低于5分钟，最长时间不得超过24h")
    private Integer scheduleEndTime;

    @Schema(description = "自定义信息，最长500个字节")
    private String customInfo;

    @Schema(description = "默认为接口调用者的用户 ID")
    private String ownerAccountId;

}
