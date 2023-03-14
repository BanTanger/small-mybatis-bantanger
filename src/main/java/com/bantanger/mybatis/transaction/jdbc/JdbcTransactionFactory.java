package com.bantanger.mybatis.transaction.jdbc;

import com.bantanger.mybatis.session.TransactionIsolationLevel;
import com.bantanger.mybatis.transaction.Transaction;
import com.bantanger.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/14 12:47
 */
public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }

}
