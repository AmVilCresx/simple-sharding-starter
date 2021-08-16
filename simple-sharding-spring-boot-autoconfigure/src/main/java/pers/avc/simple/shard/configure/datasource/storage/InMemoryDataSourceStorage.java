package pers.avc.simple.shard.configure.datasource.storage;

import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *  在内存中以Map存储数据源
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class InMemoryDataSourceStorage implements DataSourceStorage<String>{

    private final static Map<String, DataSourceMetaProp> TARGET_DATA_SOURCES_META_INFO = new HashMap<>();

    @Override
    public void storage(Map<String, DataSourceMetaProp> dataSource) {
        TARGET_DATA_SOURCES_META_INFO.putAll(dataSource);
    }

    @Override
    public DataSourceMetaProp take(String key) {
        return TARGET_DATA_SOURCES_META_INFO.get(key);
    }

    @Override
    public DataSourceMetaProp remove(String s) {
        return TARGET_DATA_SOURCES_META_INFO.remove(s);
    }
}
