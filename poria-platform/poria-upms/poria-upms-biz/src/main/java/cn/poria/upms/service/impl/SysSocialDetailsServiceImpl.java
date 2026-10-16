package cn.poria.upms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.upms.api.entity.SysSocialDetails;
import cn.poria.upms.api.vo.request.social.SocialDetailsModel;
import cn.poria.upms.api.vo.request.social.SocialDetailsQueryModel;
import cn.poria.upms.api.vo.response.social.SocialDetailsVo;
import cn.poria.upms.mapper.SysSocialDetailsMapper;
import cn.poria.upms.service.SysSocialDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysSocialDetailsServiceImpl extends ServiceImpl<SysSocialDetailsMapper, SysSocialDetails> implements SysSocialDetailsService {

    @Override
    public IPage<SocialDetailsVo> pageAccounts(SocialDetailsQueryModel model) {
        return this.page(new Page<>(model.getCurrent(), model.getSize()), new LambdaQueryWrapper<SysSocialDetails>()
                .in(CollectionUtil.isNotEmpty(model.getTypes()), SysSocialDetails::getType, model.getTypes())
                .eq(StrUtil.isNotBlank(model.getType()), SysSocialDetails::getType, model.getType())
                .eq(StrUtil.isNotBlank(model.getAppId()), SysSocialDetails::getAppId, model.getAppId())
                .like(StrUtil.isNotBlank(model.getKeywords()), SysSocialDetails::getRemark, model.getKeywords())
                .orderByDesc(SysSocialDetails::getCreateTime).orderByDesc(SysSocialDetails::getId)).convert(account -> {
            SocialDetailsVo vo = new SocialDetailsVo();
            BeanUtils.copyProperties(account, vo);
            vo.setSecretConfigured(StrUtil.isNotBlank(account.getAppSecret()));
            return vo;
        });
    }

    @Override
    public List<SocialDetailsVo> listAccounts(SocialDetailsQueryModel model) {
        return this.list(new LambdaQueryWrapper<SysSocialDetails>()
                .in(CollectionUtil.isNotEmpty(model.getTypes()), SysSocialDetails::getType, model.getTypes())
                .eq(StrUtil.isNotBlank(model.getType()), SysSocialDetails::getType, model.getType())
                .eq(StrUtil.isNotBlank(model.getAppId()), SysSocialDetails::getAppId, model.getAppId())
                .like(StrUtil.isNotBlank(model.getKeywords()), SysSocialDetails::getRemark, model.getKeywords())
                .orderByAsc(SysSocialDetails::getId)).stream().map(account -> {
            SocialDetailsVo vo = new SocialDetailsVo();
            BeanUtils.copyProperties(account, vo);
            vo.setSecretConfigured(StrUtil.isNotBlank(account.getAppSecret()));
            return vo;
        }).toList();
    }

    @Override
    public SocialDetailsVo selectAccount(SocialDetailsQueryModel model) {
        Assert.isTrue(StrUtil.isNotBlank(model.getType()), "账号类型不能为空");
        List<SysSocialDetails> accounts = this.list(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getType, model.getType())
                .eq(StrUtil.isNotBlank(model.getAppId()), SysSocialDetails::getAppId, model.getAppId())
                .last("LIMIT 2"));
        Assert.isTrue(!accounts.isEmpty(), "未配置对应的第三方账号");
        Assert.isTrue(accounts.size() == 1, "匹配到多个第三方账号，请指定唯一的AppId");
        SysSocialDetails account = accounts.getFirst();
        SocialDetailsVo vo = new SocialDetailsVo();
        BeanUtils.copyProperties(account, vo);
        vo.setSecretConfigured(StrUtil.isNotBlank(account.getAppSecret()));
        return vo;
    }

    @Override
    @Transactional
    public boolean addAccount(SocialDetailsModel model) {
        Assert.isTrue(this.count(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getType, model.getType())
                .eq(SysSocialDetails::getAppId, model.getAppId())) == 0, "该类型下的AppId已存在");
        SysSocialDetails account = new SysSocialDetails();
        BeanUtils.copyProperties(model, account);
        account.setId(null);
        account.setDelFlag("0");
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(account.getCreateTime());
        return this.save(account);
    }

    @Override
    @Transactional
    public boolean updateAccount(SocialDetailsModel model) {
        SysSocialDetails account = this.getById(model.getId());
        Assert.notNull(account, "第三方账号不存在");
        Assert.isTrue(this.count(new LambdaQueryWrapper<SysSocialDetails>()
                .eq(SysSocialDetails::getType, model.getType())
                .eq(SysSocialDetails::getAppId, model.getAppId())
                .ne(SysSocialDetails::getId, account.getId())) == 0, "该类型下的AppId已存在");
        String secret = account.getAppSecret();
        BeanUtils.copyProperties(model, account);
        if (StrUtil.isBlank(model.getAppSecret())) {
            account.setAppSecret(secret);
        }
        account.setUpdateTime(LocalDateTime.now());
        return this.updateById(account);
    }

}
