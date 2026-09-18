package cn.poria.common.data.conver.annotation;

import java.lang.annotation.*;

/**
 * author qiaodi
 * date 2025/9/22 14:03
 * version 6.7.3
 * description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReviewDatas {
    ReviewData[] value();
}
