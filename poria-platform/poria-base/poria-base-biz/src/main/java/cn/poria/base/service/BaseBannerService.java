package cn.poria.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.base.entity.BaseBanner;
import cn.poria.base.vo.request.banner.BannerModel;
import cn.poria.base.vo.request.banner.BannerQueryModel;
import cn.poria.base.vo.response.banner.BannerVo;

/**
 * banner主表(BaseBanner)表服务接口
 *
 * @author makejava
 * @since 2020-09-02 21:21:45
 */
public interface BaseBannerService extends IService<BaseBanner> {

    /**
     * 新增
     * @param model
     */
    void saveBanner(BannerModel model);

    /**
     * 编辑
     * @param model
     */
    void updateBanner(BannerModel model);

    /**
     * 删除
     * @param id
     */
    void removeBanner(String id);

    BaseBanner selectByCode(String code);


    IPage<BannerVo> bannerPage(BannerQueryModel query);
}
