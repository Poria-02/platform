package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysSocialDetails;
import cn.poria.upms.api.vo.request.social.SocialDetailsModel;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysSocialDetailsService extends IService<SysSocialDetails> {

    /**
     * 分页查询第三方账号
     *
     * @param model 分页及筛选条件
     * @return 账号分页数据
     */
    IPage<SocialDetailsVo> pageAccounts(SocialDetailsQueryModel model);

    /**
     * 查询第三方账号列表
     *
     * @param model 账号筛选条件
     * @return 账号列表
     */
    List<SocialDetailsVo> listAccounts(SocialDetailsQueryModel model);

    /**
     * 选择唯一账号，多账号时须指定AppId
     *
     * @param model 账号类型及可选AppId
     * @return 唯一匹配的账号
     */
    SocialDetailsVo selectAccount(SocialDetailsQueryModel model);

    /**
     * 新增第三方账号
     *
     * @param model 账号配置
     * @return 新增结果
     */
    boolean addAccount(SocialDetailsModel model);

    /**
     * 修改第三方账号，空密钥保留原值
     *
     * @param model 账号配置
     * @return 修改结果
     */
    boolean updateAccount(SocialDetailsModel model);
}
