package cn.poria.base.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.base.constant.AppUpdateTypeConstant;
import cn.poria.base.dao.BaseAppVersionDao;
import cn.poria.base.entity.BaseAppVersion;
import cn.poria.base.service.BaseAppVersionService;
import cn.poria.base.vo.request.BaseAppVersionModel;
import cn.poria.base.vo.response.version.AppVersionVo;
import cn.poria.base.vo.response.version.CheckVersionVo;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * (BaseAppVersion)表服务实现类
 *
 * @author makejava
 * @since 2020-08-31 17:56:25
 */
@Service("baseAppVersionService")
public class BaseAppVersionServiceImpl extends ServiceImpl<BaseAppVersionDao, BaseAppVersion> implements BaseAppVersionService {


    /**
     * 新增
     *
     * @param appVersionModel
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.APP_VERSION, allEntries = true)
    public void saveAppVersion(BaseAppVersionModel appVersionModel) {

        checkVersionNo(appVersionModel);

        BaseAppVersion version = new BaseAppVersion();
        version.setId(null);
        BeanUtils.copyProperties(appVersionModel,version);
        this.save(version);

    }

    /**
     * 查询单个
     *
     * @param id
     * @return
     */
    @Override
    public AppVersionVo selectVersionDetial(String id) {
        BaseAppVersion appVersion = this.getById(id);
        Assert.notNull(appVersion,"版本不存在");

        List<AppVersionVo> list = this.appVersionVoList(appVersion.getAppName(),appVersion.getAppOs());
        AppVersionVo vo = new AppVersionVo();
        BeanUtils.copyProperties(appVersion,vo);
        if(CollectionUtil.isNotEmpty(list) && StrUtil.equals(appVersion.getId(),list.get(0).getId())){
            vo.setIsNew(true);
        }else {
            vo.setIsNew(false);
        }
        return vo;
    }

    /**
     * 更新
     *
     * @param appVersionModel
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.APP_VERSION, allEntries = true)
    public void updateAppVersion(BaseAppVersionModel appVersionModel) {

        BaseAppVersion version = this.getById(appVersionModel.getId());
        Assert.notNull(version,"版本不存在");

        //如果是编辑的话,不是最新的版本不允许编辑版本号
        List<AppVersionVo> list = this.appVersionVoList(appVersionModel.getAppName(),appVersionModel.getAppOs());
        if(CollectionUtil.isNotEmpty(list)){
            AppVersionVo newVersion = this.appVersionVoList(appVersionModel.getAppName(),appVersionModel.getAppOs()).get(0);

            if(!StrUtil.equals(appVersionModel.getVersion(),version.getVersion())){
                if(!StrUtil.equals(newVersion.getId(),appVersionModel.getId())){
                    throw new ServiceException("历史版本版本号不能修改");
                }
            }
        }
        checkVersionNo(appVersionModel);
        BeanUtils.copyProperties(appVersionModel,version);
        this.updateById(version);

    }

    /**
     * 校验app版本
     *
     * @param appName
     * @param appOs
     * @param version
     */
    @Override
    public CheckVersionVo checkVersion(String appName, String appOs, String version) {
        List<AppVersionVo> list = appVersionVoList(appName,appOs);
        //如果版本列表空 或者传入版本大于等于最新版本
        if(CollectionUtil.isEmpty(list) || version.compareTo(list.get(0).getVersion()) >= 0){
            return null;
        }
        //初始化返回对象
        CheckVersionVo vo = new CheckVersionVo();
        vo.setUrl(list.get(0).getUrl());
        vo.setAppUpdateType(list.get(0).getAppUpdateType());
        vo.setPrompts(new ArrayList<>());
        vo.setVersion(list.get(0).getVersion());

        for(AppVersionVo versionVo : list){

            //循环到传入app版本时，终止
            if(version.compareTo(versionVo.getVersion()) >= 0){
                break;
            }

            //如果返回的比当前版本提示方式优先级低,则更新一下返回对象的提示优先级
            if(vo.getAppUpdateType().compareTo(versionVo.getAppUpdateType()) > 0 && version.compareTo(versionVo.getVersion()) < 0){
                vo.setAppUpdateType(versionVo.getAppUpdateType());
            }

            //放入升级提示
            Map<String,String> temp = new HashMap();
            temp.put("prompt",versionVo.getPrompt());
            temp.put("version",versionVo.getVersion());
            vo.getPrompts().add(temp);

            //如果提示方式是强制升级了,并且还没循环到对应的版本,也直接终止
            if(StrUtil.equals(vo.getAppUpdateType(),AppUpdateTypeConstant.MANDATORY)){
                break;
            }
        }
        return vo;

    }

    @Cacheable(value = CacheConstants.APP_VERSION,key = "#appName+':'+#appOs",unless = "#result.data.isEmpty()")
    public List<AppVersionVo> appVersionVoList(String appName,String  appOs){

        List<BaseAppVersion> list = this.list(new LambdaQueryWrapper<BaseAppVersion>()
                .eq(BaseAppVersion::getAppName,appName)
                .eq(BaseAppVersion::getAppOs,appOs)
                .orderByDesc(BaseAppVersion::getCreateTime));
        if(CollectionUtil.isEmpty(list)){
            return Collections.EMPTY_LIST;
        }
        return list.stream().map(version->{
            AppVersionVo  vo = new AppVersionVo();
            BeanUtils.copyProperties(version,vo);
            return vo;
        }).collect(Collectors.toList());
    }


    //校验版本号是否合法
    private void checkVersionNo(BaseAppVersionModel model){

        if(StrUtil.isNotBlank(model.getVersion())){

            //查询最新的一个版本
            List<AppVersionVo> list = this.appVersionVoList(model.getAppName(),model.getAppOs());
            if(CollectionUtil.isNotEmpty(list)){
                AppVersionVo appVersion = list.get(0);

                if(StrUtil.isNotBlank(model.getId())){
                    //如果id不为空说明是修改,需要比较第二个
                    if(list.size() > 1){
                        appVersion = list.get(1);
                        if(model.getVersion().compareTo(appVersion.getVersion()) <= 0){
                            throw new ServiceException("版本号不合法,小于最新版本号:" + appVersion.getVersion());
                        }
                    }
                }else if(model.getVersion().compareTo(appVersion.getVersion()) <= 0){
                    throw new ServiceException("版本号不合法,小于最新版本号:" + appVersion.getVersion());
                }
            }
        }
    }
}
