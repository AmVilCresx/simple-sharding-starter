package pers.avc.simple.shard.configure.datasource.storage;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 底层数据源存储方式
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public interface DataSourceStorage<P, S extends DataSource> {

    void storage(Map<P, S> dataSources);

    DataSource take(P p);

    DataSource remove(P p);
}
