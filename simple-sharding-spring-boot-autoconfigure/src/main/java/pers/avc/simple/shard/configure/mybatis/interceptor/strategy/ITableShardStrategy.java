package pers.avc.simple.shard.configure.mybatis.interceptor.strategy;

import org.springframework.util.StringUtils;

/**
 * 分表策略
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 *
 * @see DayTableShardStrategy
 * @see MonthTableShardStrategy
 */
public interface ITableShardStrategy {

    default String finalTableName(String baseTable) {
        if (!StringUtils.hasText(baseTable)) {
            throw new IllegalArgumentException("baseTable 不可为空..");
        }
        return baseTable;
    }
}
