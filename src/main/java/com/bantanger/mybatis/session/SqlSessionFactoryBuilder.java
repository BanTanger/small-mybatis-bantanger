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
     * configuration 作为传参向下传递
     * @param reader
     * @return
     */
    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        /*
         * <mappers>
         *     <mapper resource="mapper/User_Mapper.xml"/>
         *     <mapper resource="mapper/UserDao_Mapper.xml"/>
         *     <mapper resource="mapper/Student_Mapper.xml"/>
         * </mappers>
         *
         * xmlConfigBuilder.parse() 将 xml 里的 mappers 标签和 environment 标签进行解析，
         * 并将其封装成 mappedStatements 对象集合存放到 Configuration 里
         *
         * <mappers> <==> Map<String, MappedStatement> mappedStatements
         */
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
