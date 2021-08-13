package pers.avc.simple.shard.configure.datasource;

import cn.hutool.json.JSONUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.configure.datasource.routing.DataSourceRoutingRuler;
import pers.avc.simple.shard.configure.common.SimpleShardingConstants;
import pers.avc.simple.shard.configure.datasource.storage.DataSourceStorage;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源添加、移除操作类, 包含了 Router转换操作
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class DynamicDataSourceOperator {

    private static final Log LOGGER = LogFactory.getLog(DynamicDataSourceOperator.class);

    private final DataSourceStorage dataSourceStorage;

    private final DataSourceRoutingRuler dataSourceRoutingRuler;

    public DynamicDataSourceOperator(DataSourceStorage dataSourceStorage, DataSourceRoutingRuler dataSourceRoutingRuler){
        this.dataSourceStorage = dataSourceStorage;
        this.dataSourceRoutingRuler = dataSourceRoutingRuler;
    }

    public void addBatch(List<DataSourceMetaProp> dataSourceMetaProps) {
        LOGGER.info("批量添加动态数据源：dataSourceMetaProps.size()" + dataSourceMetaProps.size());
        if (CollectionUtils.isEmpty(dataSourceMetaProps)) {
            LOGGER.warn("添加数据源时，DataSourceMetaProp 集合为空...");
            return;
        }

        Map<Object, DataSource> targetMap = new HashMap<>();
        dataSourceMetaProps.forEach(prop -> {
            HikariDataSource dataSource = buildDynamicDataSource(prop);
            targetMap.put(dataSourceRoutingRuler.rule(prop.getUnionKey()), dataSource);
        });
        dataSourceStorage.storage(targetMap);
    }


    public void addOne(DataSourceMetaProp metaProp) {
        try {
            LOGGER.info("单个添加动态数据源：" + JSONUtil.toJsonStr(metaProp));
            String sKey = dataSourceRoutingRuler.rule(metaProp.getUnionKey());
            Map<String, HikariDataSource> ds = new HashMap<>();
            ds.put(sKey, buildDynamicDataSource(metaProp));
            dataSourceStorage.storage(ds);
        }catch (Exception e) {
            LOGGER.error("添加动态数据源异常:", e);
            throw new RuntimeException("添加动态数据源异常", e);
        }
    }

    @Nullable
    public DataSource remove(String baseRouter) {
        LOGGER.info("移除动态数据源：" + baseRouter);
        try{
            String sKey = dataSourceRoutingRuler.rule(baseRouter);
            return dataSourceStorage.remove(sKey);
        }catch (Exception e) {
            LOGGER.error("移除动态数据源:", e);
            throw new RuntimeException("移除动态数据源", e);
        }
    }

    private HikariDataSource buildDynamicDataSource(DataSourceMetaProp metaProp) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(metaProp.getDbName() + "_Pool");
        hikariConfig.setDriverClassName(metaProp.getDriverClassName());
        hikariConfig.setJdbcUrl(SimpleShardingConstants.MYSQL_PROTOCOL_PREFIX + metaProp.getDbHost() + ":" + metaProp.getDbPort() + "/" + metaProp.getDbName() + metaProp.getDbConnParameters());
        hikariConfig.setUsername(metaProp.getDbUsername());
        hikariConfig.setPassword(metaProp.getDbPassword());
        return new HikariDataSource(hikariConfig);
    }
}
