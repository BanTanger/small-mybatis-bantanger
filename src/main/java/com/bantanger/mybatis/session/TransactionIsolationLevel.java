package com.bantanger.mybatis.session;

import java.sql.Connection;

/**
 * 事务的五个隔离级别
 * @author BanTanger 半糖
 * @Date 2023/3/14 11:12
 */
public enum TransactionIsolationLevel {

    // JDBC 五个隔离级别
    NONE(Connection.TRANSACTION_NONE),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
