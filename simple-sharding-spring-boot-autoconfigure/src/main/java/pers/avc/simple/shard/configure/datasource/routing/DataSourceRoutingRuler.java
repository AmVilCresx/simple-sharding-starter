package pers.avc.simple.shard.configure.datasource.routing;

/**
 * 数据源路由规则
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 *
 * @see HashDataSourceRoutingRuler
 */
public interface DataSourceRoutingRuler {

    default String rule(String t) {
        return t;
    }
}
