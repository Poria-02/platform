package cn.poria.upms.api.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.SpringContextHolder;
import cn.poria.upms.api.feign.RemoteParamService;

public final class ParamResolver {

    public static Long getLong(String key, Long... defaultVal) {
        return checkAndGet(key, Long.class, defaultVal);
    }

    public static String getStr(String key, String... defaultVal) {
        return checkAndGet(key, String.class, defaultVal);
    }

    public static String getStr(String key, boolean isRequire) {
        String result = checkAndGet(key, String.class);
        if (isRequire && StrUtil.isBlank(result)) {
            throw new ServiceException(String.format("系统参数%s未配置", key));
        } else {
            return result;
        }
    }

    @SafeVarargs
    private static <T> T checkAndGet(String key, Class<T> clazz, T... defaultVal) {
        if (!StrUtil.isBlank(key) && defaultVal.length <= 1) {
            RemoteParamService remoteParamService = SpringContextHolder.getBean(RemoteParamService.class);
            String result = remoteParamService.publicKey(key).getData();
            if (StrUtil.isNotBlank(result)) {
                return Convert.convert(clazz, result);
            } else {
                return defaultVal.length == 1 ? Convert.convert(clazz, defaultVal[0]) : null;
            }
        } else {
            throw new IllegalArgumentException("参数不合法");
        }
    }

    private ParamResolver() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
