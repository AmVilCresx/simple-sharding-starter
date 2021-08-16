package pers.avc.simple.shard.configure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.avc.simple.shard.configure.datasource.DynamicDataSourceInitializer;
import pers.avc.simple.shard.configure.datasource.DynamicRoutingDataSource;
import pers.avc.simple.shard.configure.aspect.DynamicDataSourceCutoverAspect;
import pers.avc.simple.shard.configure.datasource.DynamicDataSourceOperator;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.configure.datasource.meta.LoadDataSourceMetaPropFactory;
import pers.avc.simple.shard.configure.datasource.routing.DataSourceRoutingRuler;
import pers.avc.simple.shard.configure.datasource.routing.DefaultDataSourceRoutingRuler;
import pers.avc.simple.shard.configure.datasource.storage.DataSourceStorage;
import pers.avc.simple.shard.configure.datasource.storage.InMemoryDataSourceStorage;
import pers.avc.simple.shard.configure.interceptor.ResetDataSourceHandlerInterceptor;
import pers.avc.simple.shard.configure.mybatis.interceptor.TableShardInterceptor;


/**
 * 自动装配
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@Configuration(proxyBeanMethods = false)
@SuppressWarnings("rawtypes")
public class SimpleShardingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "mybatis.table.shard", name = "enable", havingValue = "true")
    public TableShardInterceptor tableShardInterceptor() {
        return new TableShardInterceptor();
    }

    /**
     * 数据源切面
     */
    @Bean
    public DynamicDataSourceCutoverAspect dynamicDataSourceCutoverAspect() {
        return new DynamicDataSourceCutoverAspect();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(value = {DefaultDataSourceConfigProperties.class})
    public static class DataSourceRelationConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DynamicRoutingDataSource dynamicRoutingDataSource(DefaultDataSourceConfigProperties defaultDataSourceConfigProperties,
                                                                 DataSourceStorage dataSourceStorage) {
            DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
            dynamicRoutingDataSource.setDataSourceStorage(dataSourceStorage);
            dynamicRoutingDataSource.setDefaultDataSourceConfig(defaultDataSourceConfigProperties);
            return dynamicRoutingDataSource;
        }

        /**
         * 默认 DataSourceRoutingRuler
         *
         * @return {@link DefaultDataSourceRoutingRuler} Bean
         * @see DefaultDataSourceRoutingRuler
         */
        @Bean
        @ConditionalOnMissingBean // 不存在自定义的 DataSourceRoutingRuler Bean 才会注入 默认的规则
        @Primary // 不需要也是可以的，为了消除 idea 警告
        public DataSourceRoutingRuler defaultDataSourceRoutingRuler() {
            return new DefaultDataSourceRoutingRuler();
        }

        /**
         * 数据源存储方式
         *
         * @return {@link InMemoryDataSourceStorage}
         */
        @Bean
        @ConditionalOnMissingBean
        @Primary // 不需要也是可以的，为了消除 idea 警告
        public DataSourceStorage<String> dataSourceStorage() {
            return new InMemoryDataSourceStorage();
        }

        @Bean
        @ConditionalOnBean(value = LoadDataSourceMetaPropFactory.class)
        public DynamicDataSourceInitializer dynamicDataSourceInitialize(DynamicDataSourceOperator dynamicDataSourceOperator,
                                                                        LoadDataSourceMetaPropFactory loadDataSourceMetaPropFactory) {
            return new DynamicDataSourceInitializer(dynamicDataSourceOperator, loadDataSourceMetaPropFactory);
        }

        @Bean
        public DynamicDataSourceOperator dynamicDataSourceOperator(DataSourceStorage dataSourceStorage,
                                                                   DataSourceRoutingRuler dataSourceRoutingRuler) {
            return new DynamicDataSourceOperator(dataSourceStorage, dataSourceRoutingRuler);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public static class WebConfiguration implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new ResetDataSourceHandlerInterceptor()).addPathPatterns("/**");
        }
    }
}
