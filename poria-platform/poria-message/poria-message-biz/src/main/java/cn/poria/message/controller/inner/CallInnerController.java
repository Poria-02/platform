package cn.poria.message.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.service.CallService;
import cn.poria.message.vo.request.outbound.OutBoundModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/out/bound")
public class CallInnerController {


    @Autowired
    private CallService callService;
//    @SysLog("智能外呼")
    @PostMapping
    @Inner
    public R bound(@RequestBody OutBoundModel request){
        callService.outBound(request);
        return R.ok();
    }


    @Inner
    @GetMapping("/result/{businessId}")
    public R boundResult(@PathVariable("businessId") String businessId){
        return R.ok(callService.boundResult(businessId));
    }

}
