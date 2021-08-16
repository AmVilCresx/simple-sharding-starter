package pers.avc.simple.shard.configure.datasource.storage;

import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 底层数据源存储方式
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public interface DataSourceStorage<P> {

    void storage(Map<P, DataSourceMetaProp> dataSources);

    DataSourceMetaProp take(P p);

    DataSourceMetaProp remove(P p);
}
