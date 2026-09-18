package cn.poria.base.service;

import cn.poria.base.vo.request.banner.BaseActivityModel;
import cn.poria.base.vo.response.banner.BaseActivityVo;
import cn.poria.common.core.util.R;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.base.entity.BaseActivity;

/**
 * (BaseActivity)表服务接口
 *
 * @author makejava
 * @since 2025-11-27 15:33:02
 */
public interface BaseActivityService extends IService<BaseActivity> {

    IPage<BaseActivityVo> listPage(BaseActivityModel baseActivityModel);

    void saveActivity(BaseActivityModel baseActivityModel);

    void updateByIdActivity(BaseActivityModel baseActivityModel);
}


