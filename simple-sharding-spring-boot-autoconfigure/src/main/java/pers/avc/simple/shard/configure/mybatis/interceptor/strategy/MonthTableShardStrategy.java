package pers.avc.simple.shard.configure.mybatis.interceptor.strategy;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 一个月一张表
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
public class MonthTableShardStrategy implements ITableShardStrategy {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public String finalTableName(String baseTable) {
        if (!StringUtils.hasText(baseTable)) {
            throw new IllegalArgumentException("baseTable 不可为空..");
        }
        LocalDate localDate = LocalDate.now();
        String suffix = localDate.format(DATE_TIME_FORMATTER);
        return baseTable + "_" + suffix;
    }
}
