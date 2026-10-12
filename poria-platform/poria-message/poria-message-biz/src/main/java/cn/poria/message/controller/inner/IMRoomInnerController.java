package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.im.IMService;
import cn.poria.message.vo.request.im.CreateRoomModel;
import cn.poria.message.vo.request.im.DestroyRoomModel;
import cn.poria.message.vo.response.im.RoomInfoVo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * author qd
 * date 2024/12/11 9:17
 * description 多人会议
 */


@Hidden
@RestController
@RequestMapping("/inner/im/room")
public class IMRoomInnerController {

    @Autowired
    @Qualifier("txIMService")
    private IMService imService;

    /**
     * 创建多人会议房间
     */
    @Inner
    @PostMapping("/create")
    public R createRoom(@RequestBody @Validated CreateRoomModel model){
        imService.createRoom(model);
        return R.ok();
    }


    /**
     * 解散多人会议房间
     */
    @Inner
    @PostMapping("/destroy")
    public R destroyRoom(@RequestBody @Validated DestroyRoomModel model){
        imService.destroyRoom(model);
        return R.ok();
    }


    /**
     * 查询多人会议
     */
    @Inner
    @GetMapping("/roomInfo")
    public R<RoomInfoVo> getRoom(@RequestParam String roomId){
        return R.ok(imService.getRoom(roomId));
    }

}
