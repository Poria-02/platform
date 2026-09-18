package cn.poria.base.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.poria.base.dao.BaseBannerItemDao;
import cn.poria.base.entity.BaseBanner;
import cn.poria.base.entity.BaseBannerItem;
import cn.poria.base.service.BaseBannerItemService;
import cn.poria.base.service.BaseBannerService;
import cn.poria.base.vo.request.banner.BannerItemModel;
import cn.poria.base.vo.response.banner.AppBannerItemVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (BaseBannerItem)表服务实现类
 *
 * @author makejava
 * @since 2020-09-02 21:21:45
 */
@Service("baseBannerItemService")
public class BaseBannerItemServiceImpl extends ServiceImpl<BaseBannerItemDao, BaseBannerItem> implements BaseBannerItemService {

    @Lazy
    @Autowired
    private BaseBannerService bannerService;

    /**
     * 根据bannerId删除
     *
     * @param id
     */
    @Override
    public void removeByBannerId(String id) {
        this.remove(new LambdaQueryWrapper<BaseBannerItem>().eq(BaseBannerItem::getBannerId,id));
    }

    /**
     * 保存banner项
     *
     * @param model
     */
    @Override
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    public void saveBannerItem(BannerItemModel model) {

        BaseBannerItem bannerItem = new BaseBannerItem();
        BeanUtils.copyProperties(model,bannerItem);
        bannerItem.setId(null);
        this.save(bannerItem);
    }

    /**
     * 编辑banner项
     *
     * @param model
     */
    @Override
    @CacheEvict(value = CacheConstants.BANNER_ITEM, allEntries = true)
    public void updateBannerItem(BannerItemModel model) {
        BaseBannerItem bannerItem = this.getById(model.getId());
        Assert.notNull(bannerItem,"广告项不存在");
        BeanUtils.copyProperties(model,bannerItem);
        this.updateById(bannerItem);

    }

    /**
     * 查询广告项列表
     *
     * @param code
     * @return
     */
    @Override
    public List<AppBannerItemVo> getBannerItem(String code) {

        BaseBanner banner = bannerService.selectByCode(code);
        Assert.notNull(banner,"广告位不存在");


        LambdaQueryWrapper<BaseBannerItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseBannerItem::getBannerId,banner.getId());
        queryWrapper.eq(BaseBannerItem::getStatus,1);
        Date now = new Date();
        queryWrapper.gt(BaseBannerItem::getEndDate,now);
        queryWrapper.lt(BaseBannerItem::getBeginDate,now);
        List<BaseBannerItem> list = this.list(queryWrapper);
        if(CollectionUtil.isEmpty(list)){
            return Collections.EMPTY_LIST;
        }
        return list.stream().map(bannerItem->{
            AppBannerItemVo vo = new AppBannerItemVo();
            vo.setContent(bannerItem.getContent());
            vo.setBannerItemType(bannerItem.getBannerItemType());
            vo.setImageUrl(bannerItem.getImageUrl());
            vo.setTitle(bannerItem.getTitle());
            vo.setNeedLogin(bannerItem.getNeedLogin());
            return vo;
        }).collect(Collectors.toList());
    }
}
