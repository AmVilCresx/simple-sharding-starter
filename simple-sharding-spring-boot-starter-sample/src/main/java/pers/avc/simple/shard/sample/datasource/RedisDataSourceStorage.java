package pers.avc.simple.shard.sample.datasource;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.avc.simple.shard.configure.datasource.meta.DataSourceMetaProp;
import pers.avc.simple.shard.configure.datasource.storage.DataSourceStorage;
import pers.avc.simple.shard.sample.util.RedisUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用Redis存储数据源连接信息
 *
 * @author <a href="mailto:jzaofox@foxmail.com">AmVilCresx</a>
 */
@Component
public class RedisDataSourceStorage implements DataSourceStorage<String> {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void storage(Map<String, DataSourceMetaProp> dataSources) {
        Map<String, String> targetMap = new HashMap<>();
        dataSources.forEach((k, v) -> targetMap.put(k, JSONUtil.toJsonStr(v)));
        redisUtil.mset(targetMap);
    }

    @Override
    public DataSourceMetaProp take(String s) {
        String connectionStr = redisUtil.get(s);
        return JSONUtil.toBean(connectionStr, DataSourceMetaProp.class);
    }

    @Override
    public DataSourceMetaProp remove(String s) {
        DataSourceMetaProp sourceMetaProp = take(s);
        redisUtil.delete(s);
        return sourceMetaProp;
    }
}
