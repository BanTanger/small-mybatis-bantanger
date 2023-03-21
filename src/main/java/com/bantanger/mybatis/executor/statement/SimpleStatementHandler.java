package com.bantanger.mybatis.executor.statement;

import com.bantanger.mybatis.executor.Executor;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 简单语句处理器（STATEMENT）
 * 只执行最简单的 SQL，不设置参数
 * @author BanTanger 半糖
 * @Date 2023/3/21 15:29
 */
public class SimpleStatementHandler extends BaseStatementHandler{

    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler handler) throws SQLException {
        return null;
    }
}
