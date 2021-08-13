package pers.avc.simple.shard.configure.datasource.storage;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *  在内存中以Map存储数据源
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class InMemoryDataSourceStorage implements DataSourceStorage<String, HikariDataSource>{

    private final static Map<Object, DataSource> TARGET_DATA_SOURCES = new HashMap<>();

    @Override
    public void storage(Map<String, HikariDataSource> dataSource) {
        TARGET_DATA_SOURCES.putAll(dataSource);
    }

    @Override
    public DataSource take(String key) {
        return TARGET_DATA_SOURCES.get(key);
    }

    @Override
    public DataSource remove(String s) {
        return TARGET_DATA_SOURCES.remove(s);
    }
}
