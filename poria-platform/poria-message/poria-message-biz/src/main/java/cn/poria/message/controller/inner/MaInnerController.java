package cn.poria.message.controller.inner;

import cn.poria.message.service.WxService;
import cn.poria.message.vo.request.WxMsgSend;
import cn.poria.message.vo.request.ma.MaAccountMessageModel;
import cn.poria.message.vo.response.ma.MaAccountMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;
/**
 * 小程序
 * @author zhangchunlei
 * @date 2021年12月20日 3:08 下午
 */

@Hidden
@RestController
@RequestMapping("/inner/ma")
public class MaInnerController {

  @Autowired
  private WxService wxService;

  /**
   * 发送服务通知
   * @param msgSend
   */
  @Inner
  @PostMapping("/sendMaSubScribeMsg")
  public R sendMaSubScribeMsg(@RequestBody @Validated WxMsgSend msgSend){
    wxService.sendMaSubScribeMsg(msgSend);
    return R.ok();
  }

  /**
   * 根据过滤条件筛选小程序账号
   */
  @Inner
  @PostMapping("/account/info")
  public R<MaAccountMessageVo> findMaAccountInfo(@RequestBody MaAccountMessageModel model){
    return R.ok(wxService.findMaAccountInfo(model));
  }

}
