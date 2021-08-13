package pers.avc.simple.shard.configure.datasource.routing;

import java.util.Objects;

/**
 * 类功能描述
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class HashDataSourceRoutingRuler implements DataSourceRoutingRuler{

    @Override
    public String rule(String t) {
        return String.valueOf(Objects.hashCode(t));
    }
}
