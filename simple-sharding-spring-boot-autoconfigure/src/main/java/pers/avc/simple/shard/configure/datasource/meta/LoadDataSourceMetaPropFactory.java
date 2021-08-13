package pers.avc.simple.shard.configure.datasource.meta;

import java.util.Collections;
import java.util.List;

/**
 * 加载数据库连接的原信息
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 *
 */
public interface LoadDataSourceMetaPropFactory {

    default List<DataSourceMetaProp> loadMetaProp() {
        return Collections.emptyList();
    }
}
