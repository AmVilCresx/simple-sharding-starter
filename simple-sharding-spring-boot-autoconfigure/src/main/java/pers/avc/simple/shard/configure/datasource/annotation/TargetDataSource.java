package pers.avc.simple.shard.configure.datasource.annotation;

import java.lang.annotation.*;

/**
 * 切库注解，利用AOP实现，注意失效场景
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TargetDataSource {

    boolean toDefault() default false;
}
