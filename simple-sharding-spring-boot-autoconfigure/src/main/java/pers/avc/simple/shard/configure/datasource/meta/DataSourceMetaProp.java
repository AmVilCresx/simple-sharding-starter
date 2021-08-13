package pers.avc.simple.shard.configure.datasource.meta;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 数据源 配置信息
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class DataSourceMetaProp implements Serializable {

    private final static String MYSQL_PARAMS_SUFFIX = "?useSSL=false&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&autoReconnect=true&serverTimezone=Asia/Shanghai";

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

    /**
     * 连接后面跟的参数
     *
     * @see #MYSQL_PARAMS_SUFFIX
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

    public String getDbConnParameters() {
        return StringUtils.hasText(dbConnParameters) ? "?" + dbConnParameters : MYSQL_PARAMS_SUFFIX;
    }

    public void setDbConnParameters(String dbConnParameters) {
        this.dbConnParameters = dbConnParameters;
    }
}
