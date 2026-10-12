package cn.poria.message.vo.request.im;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * author qd
 * date 2024/12/12 13:36
 * description
 */
@Data
public class DestroyRoomModel {

    @NotBlank(message = "房间id不能为空")
    private String roomId;
}
