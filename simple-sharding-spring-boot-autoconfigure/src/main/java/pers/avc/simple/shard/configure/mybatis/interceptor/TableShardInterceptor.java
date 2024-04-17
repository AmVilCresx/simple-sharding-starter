package pers.avc.simple.shard.configure.mybatis.interceptor;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;
import pers.avc.simple.shard.configure.datasource.TableNameReplacer;
import pers.avc.simple.shard.configure.mybatis.annotation.TableShard;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.ITableShardStrategy;
import pers.avc.simple.shard.configure.mybatis.interceptor.strategy.NonTableShardStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 利用Mybatis 拦截器实现简单的分表操作
 *
 * @author <a href="mailto:jzaofox@foxmai.com">AmVilCresx</a>
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        })
public class TableShardInterceptor implements Interceptor {

    private static final Log LOGGER = LogFactory.getLog(TableShardInterceptor.class);

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

        ITableShardStrategy shardStrategy = ReflectUtil.newInstance(shardStrategyClass);
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(invocation.getArgs()[1]);
        // 原Sql
        String sqlStr = boundSql.getSql();
        LOGGER.debug(mappedStatement.getSqlCommandType() + " 处理之前的SQL: " + sqlStr);
        Map<String, String> replaceNameMappings = new HashMap<>(tableNames.length);
        for (String tableName : tableNames) {
            replaceNameMappings.put(tableName, shardStrategy.finalTableName(tableName));
        }
        String newSqlStr = regenerateNewSql(sqlStr, replaceNameMappings);
        LOGGER.debug(mappedStatement.getSqlCommandType() + " 处理之后的SQL：" + newSqlStr);
        return newSqlStr;
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

    private String regenerateNewSql(String originalSql, Map<String, String> replaceNameMappings) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(originalSql);
        TableNameReplacer nameReplacer = new TableNameReplacer(replaceNameMappings);
        return nameReplacer.generateNewSqlStr(statement);
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
