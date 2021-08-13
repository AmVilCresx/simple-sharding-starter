package pers.avc.simple.shard.configure.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import pers.avc.simple.shard.configure.config.DefaultDataSourceConfigProperties;
import pers.avc.simple.shard.configure.datasource.storage.DataSourceStorage;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 动态数据源切换
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final Log LOGGER = LogFactory.getLog(DynamicRoutingDataSource.class);

    private DataSourceStorage dataSourceStorage;

    private DefaultDataSourceConfigProperties defaultDataSourceConfigProperties;

    public void setDefaultDataSourceConfig(DefaultDataSourceConfigProperties defaultDataSourceConfigProperties) {
        this.defaultDataSourceConfigProperties = defaultDataSourceConfigProperties;
    }


    public void setDataSourceStorage(DataSourceStorage dataSourceStorage) {
        this.dataSourceStorage = dataSourceStorage;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.get();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        DataSource ds = dataSourceStorage.take(determineCurrentLookupKey());
        Objects.requireNonNull(ds, "目标数据源为空，determineCurrentLookupKey="+determineCurrentLookupKey());
        return ds;
    }

    @Override
    public void afterPropertiesSet() {
        Map<Object, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(determineCurrentLookupKey(), defaultDataSource());
        dataSourceStorage.storage(dataSourceMap);
    }

    private DataSource defaultDataSource() {
        LOGGER.info("构建默认数据源....");
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName(getClass().getName());
        jdbcConfig.setDriverClassName(defaultDataSourceConfigProperties.getDriverClassName());
        jdbcConfig.setJdbcUrl(defaultDataSourceConfigProperties.getUrl());
        jdbcConfig.setUsername(defaultDataSourceConfigProperties.getUsername());
        jdbcConfig.setPassword(defaultDataSourceConfigProperties.getPassword());
        return new HikariDataSource(jdbcConfig);
    }
}
