package pers.avc.simple.shard.configure.annotation;

import org.springframework.context.annotation.Import;
import pers.avc.simple.shard.configure.config.SimpleShardingAutoConfiguration;

import java.lang.annotation.*;

/**
 * 激活分库分表
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(SimpleShardingAutoConfiguration.class)
public @interface EnableSimpleSharding {
}
