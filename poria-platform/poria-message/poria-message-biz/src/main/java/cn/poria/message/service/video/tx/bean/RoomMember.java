package cn.poria.message.service.video.tx.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RoomMember implements Serializable {

    private static final long serialVersionUID = -40054879022143662L;

    @Schema(description = "账号id")
    private String accountId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "加入时间")
    private Date joinTime;
}
