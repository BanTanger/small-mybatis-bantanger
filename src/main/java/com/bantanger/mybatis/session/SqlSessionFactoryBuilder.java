package com.bantanger.mybatis.session;

import com.bantanger.mybatis.build.xml.XMLConfigBuilder;
import com.bantanger.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * 整个 MyBatis 的入口类，通过指定解析 XML 的 IO, 引导整个流程的启动
 * @author BanTanger 半糖
 * @Date 2023/3/13 12:58
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
