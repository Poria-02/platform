package cn.poria.common.security.xss.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;
/**
 * AutoIgnore 处理
 *
 * @author L.cm
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface AutoIgnore {
}
