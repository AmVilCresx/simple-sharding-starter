package pers.avc.simple.shard.configure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 默认数据源配置
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@ConfigurationProperties(prefix = "default.datasource")
public class DefaultDataSourceConfigProperties {

    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String url;

    private String username;

    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
