package pers.avc.simple.shard.configure.datasource;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import pers.avc.simple.shard.configure.datasource.routing.DataSourceRoutingRuler;

import java.util.Objects;

/**
 * 数据源切换协助类
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class DynamicDataSourceContextHolder {

    private static final String RANDOM_ROUTE_SUFFIX = RandomUtil.randomStringUpper(32);

    private static final String DEFAULT_ROUTE_ID = "@DEFAULT_DB_SOURCE_" + RANDOM_ROUTE_SUFFIX;

    private static final ThreadLocal<String> DATA_SOURCE_ROUTE_ID_HOLDER = ThreadLocal.withInitial(() -> DEFAULT_ROUTE_ID);

    public static String get() {
        return DATA_SOURCE_ROUTE_ID_HOLDER.get();
    }

    public static void set(String routerValue) {
        if (Objects.equals(routerValue, DEFAULT_ROUTE_ID)) {
            reset();
            return;
        }
        DataSourceRoutingRuler routingRuler = SpringUtil.getApplicationContext().getBean(DataSourceRoutingRuler.class);
        DATA_SOURCE_ROUTE_ID_HOLDER.set(routingRuler.rule(routerValue));
    }

    public static void reset() {
        DATA_SOURCE_ROUTE_ID_HOLDER.set(DEFAULT_ROUTE_ID);
    }
}
