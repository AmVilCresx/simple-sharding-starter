package pers.avc.simple.shard.configure.mybatis.annotation;


import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.ITableShardStrategy;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.NonTableShardStrategy;

import java.lang.annotation.*;

/**
 * 分表注解, 用于Mapper接口
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableShard {

    String[] tableNames() default {};

    Class<? extends ITableShardStrategy> strategy() default NonTableShardStrategy.class;
}
