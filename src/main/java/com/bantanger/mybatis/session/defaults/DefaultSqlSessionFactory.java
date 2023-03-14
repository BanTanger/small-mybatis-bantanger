package com.bantanger.mybatis.session.defaults;

import com.bantanger.mybatis.binding.MapperRegistry;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.SqlSession;
import com.bantanger.mybatis.session.SqlSessionFactory;

/**
 * DefaultSqlSession 的配置工厂，MyBatis 中的核心类
 * @author BanTanger 半糖
 * @Date 2023/3/12 19:56
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }

}
