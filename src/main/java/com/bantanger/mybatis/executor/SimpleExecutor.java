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

    /**
     * 执行器底层执行 JDBC 代码
     * 1. 加载驱动，获取数据库连接
     * 2.
     */
    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            // 实例化连接：连接数据库(获取数据库连接)，设置事务隔离级别
            Connection connection = transaction.getConnection();
            // 实例化，设置 SQL 执行超时时间、可扩展
            Statement stmt = handler.prepare(connection);
            // 参数化
            handler.parameterize(stmt);
            // 执行查询
            return handler.query(stmt, resultHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
