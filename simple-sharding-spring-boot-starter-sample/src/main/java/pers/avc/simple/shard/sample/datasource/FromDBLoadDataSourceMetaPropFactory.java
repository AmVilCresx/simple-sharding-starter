package pers.avc.simple.shard.sample.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.configure.datasource.meta.LoadDataSourceMetaPropFactory;
import pers.avc.simple.shard.sample.mapper.DataSourceServerConfigMapper;
import pers.avc.simple.shard.sample.model.datasource.DataSourceServerConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  从数据库表中加载数据库连接的原信息
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Component
public class FromDBLoadDataSourceMetaPropFactory implements LoadDataSourceMetaPropFactory {

   @Autowired
    private DataSourceServerConfigMapper dataSourceServerConfigMapper;

    @Override
    public List<DataSourceMetaProp> loadMetaProp() {
        List<DataSourceServerConfig> dataSourceServerConfigs = dataSourceServerConfigMapper.listEnabledConfig();
        if (CollectionUtils.isEmpty(dataSourceServerConfigs)) {
            return Collections.emptyList();
        }

       return dataSourceServerConfigs.stream().map(config -> {
            DataSourceMetaProp metaProp = new DataSourceMetaProp();
            metaProp.setUnionKey(String.valueOf(config.getId()));
            metaProp.setDbHost(config.getDbHost());
            metaProp.setDbPort(config.getDbPort());
            metaProp.setDbName(config.getDbName());
            metaProp.setDbUsername(config.getDbUsername());
            metaProp.setDbPassword(config.getDbPassword());
            metaProp.setDbConnParameters(config.getDbConnParameters());
           return metaProp;
        }).collect(Collectors.toList());
    }
}
