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

    /**
     * 解析配置文件（dom4j + xpath），封装 Configuration
     * @param reader
     * @return
     */
    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    /**
     * 创建 SqlSessionFactory 对象（调度默认工厂）
     * @param config
     * @return
     */
    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
