package cn.poria.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.base.entity.BaseBannerItem;
import cn.poria.base.vo.request.banner.BannerItemModel;
import cn.poria.base.vo.response.banner.AppBannerItemVo;

import java.util.List;

/**
 * (BaseBannerItem)表服务接口
 *
 * @author makejava
 * @since 2020-09-02 21:21:45
 */
public interface BaseBannerItemService extends IService<BaseBannerItem> {

    /**
     * 根据bannerId删除
     * @param id
     */
    void removeByBannerId(String id);

    /**
     * 保存banner项
     * @param model
     */
    void saveBannerItem(BannerItemModel model);

    /**
     * 编辑banner项
     * @param model
     */
    void updateBannerItem(BannerItemModel model);

    /**
     * 查询广告项列表
     * @param code
     * @return
     */
    List<AppBannerItemVo> getBannerItem(String code);
}
