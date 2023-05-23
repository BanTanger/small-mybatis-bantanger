package com.bantanger.mybatis.session.defaults;

import com.bantanger.mybatis.executor.Executor;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.Environment;
import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 默认实现 SqlSession
 * @author BanTanger 半糖
 * @Date 2023/3/10 18:37
 */
public class DefaultSqlSession implements SqlSession {

    private final Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);

    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);

        // 具体操作委派给底层执行器执行
        List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
        try {
            return list.get(0);
        } catch (Exception e) {
            logger.info("库表中未查询到结果");
            return null;
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

}
