package pers.avc.simple.shard.configure.datasource;

import cn.hutool.core.collection.CollUtil;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.*;

/**
 * 表名映射替换
 */
public class TableNameReplacer extends TablesNamesFinder {

    private List<String> tables;

    private final Map<String, String> replaceNameMappings;

    private String originalTable;

    private String alias;

    public TableNameReplacer(Map<String, String> replaceNameMappings) {
        this.replaceNameMappings = replaceNameMappings;
    }

    public String generateNewSqlStr(Statement statement) {
        this.getTables(statement);
        return statement.toString();
    }

    @Override
    public void visit(Table tableName) {
        String tableWholeName = tableName.getFullyQualifiedName();
        Alias alias = tableName.getAlias();
        if (Objects.nonNull(alias)) {
            this.alias = alias.toString();
        }
        originalTable = tableWholeName;
        if (!this.tables.contains(tableWholeName)) {
            this.tables.add(tableWholeName);
        }
    }

    @Override
    public List<String> getTableList(Statement statement) {
        this.init();
        statement.accept(this);
        return this.tables;
    }

    @Override
    public Set<String> getTables(Statement statement) {
        this.init();
        statement.accept(this);
        if (CollUtil.isEmpty(this.tables)) {
            return new HashSet<>();
        }
        return new HashSet<>(this.tables);
    }

    protected void init() {
        this.tables = new ArrayList<>();
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem<?> item : plainSelect.getSelectItems()) {
                item.accept(this);
            }
        }

        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
            Table table = new Table(replaceNameMappings.getOrDefault(originalTable, originalTable));
            if (Objects.nonNull(alias)) {
                table.setAlias(new Alias(alias));
            }
            plainSelect.setFromItem(table);

        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                Table fromItem = (Table)join.getFromItem();
                String name = fromItem.getName();
                fromItem.setName(replaceNameMappings.getOrDefault(name, name));
            }
        }

        Expression selectWhere = plainSelect.getWhere();
        if (selectWhere != null) {
            selectWhere.accept(this);
        }

        if (plainSelect.getOracleHierarchical() != null) {
            plainSelect.getOracleHierarchical().accept(this);
        }
    }

    @Override
    public void visit(Insert insert) {
        Table table = insert.getTable();
        String tableName = table.getName();
        table.setName(replaceNameMappings.getOrDefault(tableName, tableName));
        insert.setTable(table);
    }

    @Override
    public void visit(Update update) {
        Table updateTable = update.getTable();
        String name = updateTable.getName();
        updateTable.setName(replaceNameMappings.getOrDefault(name, name));
        update.setTable(updateTable);
        List<Join> joins = update.getJoins();
        if (CollUtil.isNotEmpty(joins)) {
            for (Join join : joins) {
                Table fromItem = (Table)join.getFromItem();
                String joinName = fromItem.getName();
                fromItem.setName(replaceNameMappings.getOrDefault(joinName, joinName));
            }
        }
        Expression where = update.getWhere();
        if (Objects.nonNull(where)) {
            where.accept(this);
        }

        OracleHint oracleHint = update.getOracleHint();
        if (Objects.nonNull(oracleHint)) {
            oracleHint.accept(this);
        }
    }

    @Override
    public void visit(Delete delete) {
        Table deleteTable = delete.getTable();
        String delTableName = deleteTable.getName();
        deleteTable.setName(replaceNameMappings.getOrDefault(delTableName, delTableName));

        List<Join> joins = delete.getJoins();
        if (CollUtil.isNotEmpty(joins)) {
            for (Join join : joins) {
                Table fromItem = (Table)join.getFromItem();
                String joinName = fromItem.getName();
                fromItem.setName(replaceNameMappings.getOrDefault(joinName, joinName));
            }
        }
        Expression where = delete.getWhere();
        if (Objects.nonNull(where)) {
            where.accept(this);
        }

        OracleHint oracleHint = delete.getOracleHint();
        if (Objects.nonNull(oracleHint)) {
            oracleHint.accept(this);
        }
    }

    @Override
    public void visit(Drop drop) {
        Table droTable = drop.getName();
        String droTableName = droTable.getName();
        drop.setIfExists(true);
        droTable.setName(replaceNameMappings.getOrDefault(droTableName, droTableName));
    }

    @Override
    public void visit(Truncate truncate) {
        Table truncateTable = truncate.getTable();
        String truncateTableName = truncateTable.getName();
        truncateTable.setName(replaceNameMappings.getOrDefault(truncateTableName, truncateTableName));
    }
}