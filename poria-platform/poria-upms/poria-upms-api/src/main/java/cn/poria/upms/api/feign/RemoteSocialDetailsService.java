package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 第三方账号内部查询契约
 */
@FeignClient(name = "remoteSocialDetailsService", url = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteSocialDetailsService {

    /**
     * 查询账号列表，不返回应用密钥
     *
     * @param model 账号筛选条件
     * @param from 内部调用来源标识
     * @return 账号列表
     */
    @PostMapping("/inner/social/details/list")
    R<List<SocialDetailsVo>> list(@RequestBody SocialDetailsQueryModel model, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 按类型和AppId选择账号，未指定AppId时要求该类型仅配置一个账号
     *
     * @param model 账号类型及可选AppId
     * @param from 内部调用来源标识
     * @return 唯一匹配的账号
     */
    @PostMapping("/inner/social/details/account")
    R<SocialDetailsVo> account(@RequestBody SocialDetailsQueryModel model, @RequestHeader(SecurityConstants.FROM) String from);
}
