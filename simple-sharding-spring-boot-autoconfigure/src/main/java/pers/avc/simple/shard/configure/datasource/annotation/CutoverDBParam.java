package pers.avc.simple.shard.configure.datasource.annotation;

import java.lang.annotation.*;

/**
 * 用于参数， 配合 {@link TargetDataSource} 使用，用来标识哪个字段来切库
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CutoverDBParam {

    String fieldName() default "";
}
