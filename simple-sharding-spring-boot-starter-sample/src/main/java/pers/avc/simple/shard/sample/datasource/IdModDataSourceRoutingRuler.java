package pers.avc.simple.shard.sample.datasource;


import pers.avc.simple.shard.configure.datasource.routing.DataSourceRoutingRuler;

/**
 * Id 取模 规则
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class IdModDataSourceRoutingRuler implements DataSourceRoutingRuler {

    /**
     * 修改此值 要注意数据的重新分布
     */
    private final int baseMod;

    public IdModDataSourceRoutingRuler(int baseMod) {
        this.baseMod = baseMod;
    }

    @Override
    public String rule(String key) {
        long id = Long.parseLong(key);
        return String.valueOf(id % baseMod);
    }
}
