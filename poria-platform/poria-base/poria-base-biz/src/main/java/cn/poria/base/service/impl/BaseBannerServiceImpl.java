package cn.poria.base.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.base.dao.BaseBannerDao;
import cn.poria.base.entity.BaseBanner;
import cn.poria.base.service.BaseBannerItemService;
import cn.poria.base.service.BaseBannerService;
import cn.poria.base.vo.request.banner.BannerModel;
import cn.poria.base.vo.request.banner.BannerQueryModel;
import cn.poria.base.vo.response.banner.BannerVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * banner主表(BaseBanner)表服务实现类
 *
 * @author makejava
 * @since 2020-09-02 21:21:45
 */
@Slf4j
@Service("baseBannerService")
public class BaseBannerServiceImpl extends ServiceImpl<BaseBannerDao, BaseBanner> implements BaseBannerService {

    @Autowired
    private BaseBannerItemService bannerItemService;


    @Override
    public IPage<BannerVo> bannerPage(BannerQueryModel query) {
        Page page = new Page(query.getCurrent(),query.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<BaseBanner> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StrUtil.isNotBlank(query.getTitle()),BaseBanner::getTitle,query.getTitle());
        queryWrapper.eq(query.getStatus()!=null,BaseBanner::getStatus,query.getStatus());
        return this.page(page,queryWrapper).convert(banner->{
            BannerVo vo = new BannerVo();
            BeanUtils.copyProperties(banner,vo);
            return vo;
        });
    }

    /**
     * 新增
     *
     * @param model
     */
    @Override
    @Transactional
    public void saveBanner(BannerModel model) {

        Assert.isNull(selectByCode(model.getCode()),"code:{}已存在",model.getCode());

        BaseBanner banner = new BaseBanner();
        BeanUtils.copyProperties(model,banner);

        this.save(banner);
    }

    /**
     * 编辑
     *
     * @param model
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    public void updateBanner(BannerModel model) {
        BaseBanner banner = this.getById(model.getId());
        Assert.notNull(banner,"banner不存在");

        if(!StrUtil.equals(model.getCode(),banner.getCode())){
            Assert.isNull(selectByCode(model.getCode()),"code:{}已存在",model.getCode());
        }

        BeanUtils.copyProperties(model,banner);
        this.updateById(banner);

    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    public void removeBanner(String id) {
        this.removeById(id);
        bannerItemService.removeByBannerId(id);
    }

    @Override
    public BaseBanner selectByCode(String code) {
        return this.getOne(new LambdaQueryWrapper<BaseBanner>().eq(BaseBanner::getCode,code));
    }
}
