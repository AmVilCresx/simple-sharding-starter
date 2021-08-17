package pers.avc.simple.shard.configure.mybatis.interceptor;

import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.ITableShardStrategy;
import pers.avc.simple.shard.configure.mybatis.annotation.TableShard;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.NonTableShardStrategy;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pers.avc.simple.shard.configure.common.SimpleShardingConstants.LEFT_BRACKET;
import static pers.avc.simple.shard.configure.common.SimpleShardingConstants.POINT;

/**
 * 利用Mybatis 拦截器实现简单的分表操作
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        })
public class TableShardInterceptor implements Interceptor {

    private static final Log LOGGER = LogFactory.getLog(TableShardInterceptor.class);

    private static final Pattern SQL_PATTERN = Pattern.compile("\\s+|\t|\r|\n");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String newSql = generatorFinalSql(invocation, mappedStatement);
        if (StringUtils.hasText(newSql)) {
            // 用新sql代替旧sql
            BoundSql boundSql = mappedStatement.getBoundSql(args[1]);
            BoundSql boundSqlNew = new BoundSql(mappedStatement.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            MappedStatement ms = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(boundSqlNew));
            args[0] = ms;
        }

        return invocation.proceed();
    }

    private String generatorFinalSql(Invocation invocation, MappedStatement mappedStatement) throws Throwable {
        // 接口全限定名.方法名称
        String id = mappedStatement.getId();
        String interfaceName = id.substring(0, id.lastIndexOf("."));
        Class<?> interfaceClazz = Class.forName(interfaceName);
        if (!interfaceClazz.isInterface()) {
            LOGGER.error("@TableShard 只能标注在接口上...");
            throw new UnsupportedOperationException("@TableShard 只能标注在接口上...");
        }
        TableShard tableShardAnno = interfaceClazz.getAnnotation(TableShard.class);
        if (Objects.isNull(tableShardAnno)) {
            LOGGER.debug("接口【" + interfaceClazz + "】没有标注 【@TableShard】，不再进行后续分表操作... ");
            return null;
        }

        Class<? extends ITableShardStrategy> shardStrategyClass = tableShardAnno.strategy();
        if (NonTableShardStrategy.class.isAssignableFrom(shardStrategyClass)) {
            // 如果是默认不分表策略，直接放行，减少后续操作
            LOGGER.debug("分表策略为 NonTableShardStrategy, 即不分表，不再进行后续分表操作...");
            return null;
        }

        String[] tableNames = tableShardAnno.tableNames();
        if (ArrayUtil.isEmpty(tableNames)) {
            LOGGER.debug("分表的 tableNames 为空， 不再进行后续分表操作...");
            return null;
        }

        ITableShardStrategy shardStrategy = shardStrategyClass.newInstance();
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(invocation.getArgs()[1]);
        // 处理不可见字符（制表符、回车、换行）
        String sqlStr = adjustSql(boundSql.getSql());
        LOGGER.debug(mappedStatement.getSqlCommandType() + " 处理之前的SQL: " + sqlStr);
        for (String tableName : tableNames) {
            boolean suffixBlank = !sqlStr.endsWith(tableName);
            // 原始表名.字段名 ====》 新表名.字段名，如： t_user.name ===> t_user_xxx.name
            sqlStr = sqlStr.replace(tableName + POINT, shardStrategy.finalTableName(tableName) + POINT);
            // 原始表名( ====》 新表名(，如： insert into t_user( ===> insert into t_user_xxx(
            sqlStr = sqlStr.replace(tableName + LEFT_BRACKET, shardStrategy.finalTableName(tableName) + LEFT_BRACKET);
            sqlStr = sqlStr.replace(fillUpBlank(tableName, suffixBlank), fillUpBlank(shardStrategy.finalTableName(tableName), suffixBlank));
        }
        LOGGER.debug(mappedStatement.getSqlCommandType() + " 处理之后的SQL：" + sqlStr);
        return sqlStr;
    }


    @Override
    public Object plugin(Object target) {
        // 当目标类是 Executor 类型时，才包装目标类，否者直接返回目标本身, 减少目标被代理的次数
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * 表名前后的空格处理，防止跟字段名冲突
     *
     * @param tabName     数据库表名
     * @param suffixBlank 是否需要给后面追加空格， 如果以表名结尾，那就不需要了
     * @return " "+表名 + " "
     */
    private String fillUpBlank(String tabName, boolean suffixBlank) {
        String newTableName = " " + tabName;
        return suffixBlank ? newTableName + " " : newTableName;
    }

    /**
     * 调整SQL，去掉 制表符、回车、换行等不可见字符串, 拉伸成一个长字符串
     *
     * @param sql 原始SQL
     * @return 处理之后的SQL
     */
    private static String adjustSql(String sql) {
        Matcher m = SQL_PATTERN.matcher(sql);
        return m.replaceAll(" ");
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * 封装 新构建的 SqlSource
     */
    public static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
