package cn.poria.message.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.integration.customer.vo.UserAccountBindVo;
import cn.poria.message.integration.customer.vo.response.company.CusCompanyInnerVo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** 消息服务使用的远程接口契约，保留原有 HTTP 路径和响应格式。 */
@Hidden
@FeignClient(name = "remoteCustomerApi", url = "${PORIA_CUSTOMER:http://poria-customer:8004}")
public interface RemoteCustomerApi {
    @GetMapping("/inner/company/{id}")
    R<CusCompanyInnerVo> findCompanyInfo(@PathVariable("id") String id, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/account/openId/{appId}/{accountId}")
    R<String> getBindOpenId(@PathVariable("accountId") String accountId,
                            @PathVariable("appId") String appId,
                            @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/account/binds/{accountId}")
    R<List<UserAccountBindVo>> findUserBinds(@PathVariable("accountId") String accountId, @RequestHeader(SecurityConstants.FROM) String from);
}
