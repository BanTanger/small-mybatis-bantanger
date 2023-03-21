package com.bantanger.mybatis.session;

import com.bantanger.mybatis.binding.MapperRegistry;
import com.bantanger.mybatis.dataSource.druid.DruidDataSourceFactory;
import com.bantanger.mybatis.dataSource.pooled.PooledDataSourceFactory;
import com.bantanger.mybatis.dataSource.unpooled.UnpooledDataSourceFactory;
import com.bantanger.mybatis.executor.Executor;
import com.bantanger.mybatis.executor.SimpleExecutor;
import com.bantanger.mybatis.executor.resultset.DefaultResultSetHandler;
import com.bantanger.mybatis.executor.resultset.ResultSetHandler;
import com.bantanger.mybatis.executor.statement.PreparedStatementHandler;
import com.bantanger.mybatis.executor.statement.StatementHandler;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.Environment;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.transaction.Transaction;
import com.bantanger.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.bantanger.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis 核心存储对象的配置类
 * 存储 mybatis-config-datasource 解析的内容
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:00
 */
public class Configuration {

    /**
     * 环境
     */
    protected Environment environment;

    /**
     * 映射注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * 类型别名注册机
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /**
     * 解析 mapper.xml 配置文件中解析出来的 sql 语句
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    /**
     * 创建结果集处理器
     * @return
     * @param executor
     * @param mappedStatement
     * @param boundSql
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
