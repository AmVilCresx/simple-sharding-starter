package pers.avc.simple.shard.configure.datasource;

import cn.hutool.cache.impl.LRUCache;
import cn.hutool.json.JSONUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.config.DefaultDataSourceConfigProperties;
import pers.avc.simple.shard.configure.datasource.event.RemoveDataSourceEvent;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
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

    private final LRUCache<String, DataSource> DATASOURCE_CACHE = new LRUCache<>(128);

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
        String lookUpKey = String.valueOf(determineCurrentLookupKey());
        DataSourceMetaProp dsMetaInfo = dataSourceStorage.take(lookUpKey);
        Objects.requireNonNull(dsMetaInfo, "目标数据源为空，determineCurrentLookupKey="+determineCurrentLookupKey());
        if (DATASOURCE_CACHE.containsKey(lookUpKey)) {
            return DATASOURCE_CACHE.get(lookUpKey);
        }
        HikariDataSource dataSource = DynamicDataSourceOperator.buildDynamicDataSource(dsMetaInfo);
        DATASOURCE_CACHE.put(lookUpKey, dataSource);
        return dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        Map<Object, DataSourceMetaProp> dataSourceMap = new HashMap<>();
        dataSourceMap.put(determineCurrentLookupKey(), defaultDataSource());
        dataSourceStorage.storage(dataSourceMap);
    }

    private DataSourceMetaProp defaultDataSource() {
        LOGGER.info("构建默认数据源信息....");
        DataSourceMetaProp sourceMetaProp = new DataSourceMetaProp();
        BeanUtils.copyProperties(defaultDataSourceConfigProperties, sourceMetaProp);
        return sourceMetaProp;
    }


    @EventListener(value = RemoveDataSourceEvent.class)
    public void removeDataSourceEventListener(RemoveDataSourceEvent dataSourceEvent) {
        if (Objects.isNull(dataSourceEvent) || !StringUtils.hasText(dataSourceEvent.getLookUpKey())) {
            LOGGER.warn("移除数据源事件对象不合法:dataSourceEvent=" + JSONUtil.toJsonStr(dataSourceEvent));
            return;
        }
        DATASOURCE_CACHE.remove(dataSourceEvent.getLookUpKey());
    }
}
