package cn.poria.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.base.entity.BaseAppVersion;
import cn.poria.base.vo.request.BaseAppVersionModel;
import cn.poria.base.vo.response.version.AppVersionVo;
import cn.poria.base.vo.response.version.CheckVersionVo;

/**
 * (BaseAppVersion)表服务接口
 *
 * @author makejava
 * @since 2020-08-31 17:56:25
 */
public interface BaseAppVersionService extends IService<BaseAppVersion> {

    /**
     * 新增
     * @param appVersionModel
     */
    void saveAppVersion(BaseAppVersionModel appVersionModel);

    /**
     * 更新
     * @param appVersionModel
     */
    void updateAppVersion(BaseAppVersionModel appVersionModel);

    /**
     * 校验app版本
     * @param appName
     * @param appOs
     * @param version
     */
    CheckVersionVo checkVersion(String appName, String appOs, String version);

    /**
     * 查询单个
     * @param id
     * @return
     */
    AppVersionVo selectVersionDetial(String id);
}
