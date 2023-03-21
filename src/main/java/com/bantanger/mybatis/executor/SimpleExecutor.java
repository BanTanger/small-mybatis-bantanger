package com.bantanger.mybatis.executor;

import com.bantanger.mybatis.executor.statement.StatementHandler;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.ResultHandler;
import com.bantanger.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/19 19:43
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            Statement stmt = handler.prepare(connection);
            handler.parameterize(stmt);
            return handler.query(stmt, resultHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
