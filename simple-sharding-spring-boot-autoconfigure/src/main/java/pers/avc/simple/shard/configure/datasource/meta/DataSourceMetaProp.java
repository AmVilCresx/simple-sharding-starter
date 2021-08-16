package pers.avc.simple.shard.configure.datasource.meta;

import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.common.SimpleShardingConstants;

import java.io.Serializable;

/**
 * 数据源 配置信息
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class DataSourceMetaProp implements Serializable {

    /**
     * 用于路由，做唯一标识，不可重复
     */
    private String unionKey;

    private String driverClassName;

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
     * @see SimpleShardingConstants#MYSQL_PARAMS_SUFFIX
     */
    private String dbConnParameters;

    public String getUnionKey() {
        return unionKey;
    }

    public void setUnionKey(String unionKey) {
        this.unionKey = unionKey;
    }

    public String getDriverClassName() {
        return StringUtils.hasText(driverClassName) ? driverClassName : "com.mysql.cj.jdbc.Driver";
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
