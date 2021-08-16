package pers.avc.simple.shard.configure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.common.SimpleShardingConstants;

import java.io.Serializable;

/**
 * 默认数据源配置
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@ConfigurationProperties(prefix = "default.datasource")
public class DefaultDataSourceConfigProperties implements Serializable {

    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String dbHost;

    private String dbName;

    private Integer dbPort;

    private String dbUsername;

    private String dbPassword;

    private Long connectionTimeout;

    private Integer minIdle;

    private Integer maxPoolSize;

    private Long maxLifetime;

    private Boolean isAutoCommit;

    /**
     * 连接后面跟的参数
     *
     * @see pers.avc.simple.shard.configure.common.SimpleShardingConstants#MYSQL_PARAMS_SUFFIX
     */
    private String dbConnParameters;


    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(Long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public Boolean getAutoCommit() {
        return isAutoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        isAutoCommit = autoCommit;
    }

    public String getDbConnParameters() {
        if (StringUtils.hasText(dbConnParameters)) {
            if (dbConnParameters.startsWith("?")) {
                return dbConnParameters;
            }
            return "?" + dbConnParameters;
        }
        return SimpleShardingConstants.MYSQL_PARAMS_SUFFIX;
    }

    public void setDbConnParameters(String dbConnParameters) {
        this.dbConnParameters = dbConnParameters;
    }
}
