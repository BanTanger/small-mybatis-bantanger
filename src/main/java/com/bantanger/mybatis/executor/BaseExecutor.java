package com.bantanger.mybatis.executor;

import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.ResultHandler;
import com.bantanger.mybatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/19 19:22
 */
public abstract class BaseExecutor implements Executor {

    private final Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

    /*
     * 将对象设置成全局，便于后续 close 方法释放资源
     */

    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;

    private boolean closed;

    public BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler handler, BoundSql boundSql) {
        if (closed) {
            throw new RuntimeException("Executor was closed");
        }
        return doQuery(ms, parameter, handler, boundSql);
    }

    /**
     * 执行器底层执行 JDBC 代码
     */
    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler handler, BoundSql boundSql);

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed");
        }
        return transaction;
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        } catch (SQLException e) {
            logger.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }
}
