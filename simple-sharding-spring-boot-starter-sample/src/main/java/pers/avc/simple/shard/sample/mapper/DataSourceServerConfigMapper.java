package pers.avc.simple.shard.sample.mapper;

import org.springframework.stereotype.Repository;
import pers.avc.simple.shard.sample.model.datasource.DataSourceServerConfig;

import java.util.List;

/**
 * {@link DataSourceServerConfig} Mapper
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Repository
public interface DataSourceServerConfigMapper {

    List<DataSourceServerConfig> listEnabledConfig();
}
