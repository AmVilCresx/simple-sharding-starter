package pers.avc.simple.shard.configure.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.CollectionUtils;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.configure.datasource.meta.LoadDataSourceMetaPropFactory;

import java.util.List;

/**
 * 初始化动态数据源
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class DynamicDataSourceInitializer implements CommandLineRunner {

    private static final Log LOGGER = LogFactory.getLog(DynamicDataSourceInitializer.class);

    private final DynamicDataSourceOperator dynamicDataSourceOperator;

    private final LoadDataSourceMetaPropFactory loadDataSourceMetaPropFactory;

    public DynamicDataSourceInitializer(DynamicDataSourceOperator dynamicDataSourceOperator, LoadDataSourceMetaPropFactory loadDataSourceMetaPropFactory) {
        this.dynamicDataSourceOperator = dynamicDataSourceOperator;
        this.loadDataSourceMetaPropFactory = loadDataSourceMetaPropFactory;
    }


    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("开始初始化多个数据源....");
        List<DataSourceMetaProp> sourceMetaProps = loadDataSourceMetaPropFactory.loadMetaProp();
        if (CollectionUtils.isEmpty(sourceMetaProps)) {
            LOGGER.warn("数据源配置【sourceMetaProps】为空..");
            return;
        }

        dynamicDataSourceOperator.addBatch(sourceMetaProps);
    }
}
