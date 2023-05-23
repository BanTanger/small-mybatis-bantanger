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
 * MyBatis 核心存储对象的全局配置对象
 * 存储 mybatis-config-datasource.xml 解析的内容以及 XX_Mapper.xml 的内容
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:00
 */
public class Configuration {

    /**
     * 环境变量
     * 将 <environment> 标签里的内容解析到 Environment 对象里
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
     * 一级缓存
     * 解析 mapper.xml 配置文件中解析出来的 sql 标签,每个 标签 都被解析成 MappedStatement
     * key:statementId=namespace.id; value:封装好的 MappedStatement 对象
     * Configuration 将所有的标签对象封装成 map 集合传递给底层 jdbc 进行使用
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public Configuration() {
        // 注册事务工厂的别名
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        // 注册数据源的别名
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

    /**
     * 优雅方式封装 mappedStatement 到 Configuration，直接调用这个方法进行参数传递即可
     * @param ms
     */
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    /**
     * 通过 mappedStatementId 获取具体 MappedStatement 对象
     * @param id
     * @return
     */
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
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 创建执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建 statement 对象处理器进行 sql 参数预处理
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
