package cn.poria.base.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.poria.base.vo.request.banner.BaseActivityModel;
import cn.poria.base.vo.response.banner.BaseActivityVo;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.base.dao.BaseActivityDao;
import cn.poria.base.entity.BaseActivity;
import cn.poria.base.service.BaseActivityService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (BaseActivity)表服务实现类
 *
 * @author makejava
 * @since 2025-11-27 15:33:02
 */
@Service("baseActivityService")
public class BaseActivityServiceImpl extends ServiceImpl<BaseActivityDao, BaseActivity> implements BaseActivityService {

    @Override
    public IPage<BaseActivityVo> listPage(BaseActivityModel baseActivityModel) {
        Page<BaseActivity> page = new Page<>(baseActivityModel.getCurrent(), baseActivityModel.getSize());
        this.page(page, new LambdaQueryWrapper<BaseActivity>()
                .like(StringUtils.isNotBlank(baseActivityModel.getName()), BaseActivity::getName, baseActivityModel.getName())
                .in(ObjectUtils.isNotEmpty(baseActivityModel.getIsStatus()), BaseActivity::getIsStatus, baseActivityModel.getIsStatus())
        );
        return page.convert(e -> {
            BaseActivityVo vo = new BaseActivityVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        });
    }

    @Override
    public void saveActivity(BaseActivityModel baseActivityModel) {
        //判断名称是否存在
        List<BaseActivity> baseActivity = this.list(new LambdaQueryWrapper<BaseActivity>()
                .eq(BaseActivity::getName, baseActivityModel.getName())
        );
        if(baseActivity.size() > 0 ){
            throw new ServiceException("名称已存在");
        }else{
            BaseActivity baseActivity1 = new BaseActivity();
            BeanUtils.copyProperties(baseActivityModel, baseActivity1);
            this.save(baseActivity1);
            baseActivity1.setTitleParameter(baseActivityModel.getTitleParameter() + "?id=" + baseActivity1.getId());
            this.updateById(baseActivity1);
        }
    }

    @Override
    public void updateByIdActivity(BaseActivityModel baseActivityModel) {

        //判断名称是否存在，除开当前记录外
        List<BaseActivity> baseActivity = this.list(new LambdaQueryWrapper<BaseActivity>()
                .eq(BaseActivity::getName, baseActivityModel.getName())
                .ne(BaseActivity::getId, baseActivityModel.getId())
        );
        if(baseActivity.size() > 0 ){
            throw new ServiceException("名称已存在");
        }else{
            BaseActivity baseActivity1 = new BaseActivity();
            BeanUtils.copyProperties(baseActivityModel, baseActivity1);
            this.updateById(baseActivity1);
        }

    }
}


