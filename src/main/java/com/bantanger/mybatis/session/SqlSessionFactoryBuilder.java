package com.bantanger.mybatis.session;

import com.bantanger.mybatis.build.xml.XMLConfigBuilder;
import com.bantanger.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * 整个 MyBatis 的入口类，职责是构建 SqlSessionFactory 对象, 并解析 XML 文件, 引导整个流程的启动
 * @author BanTanger 半糖
 * @Date 2023/3/13 12:58
 */
public class SqlSessionFactoryBuilder {

    /**
     * 1. 创建 SAXReader(dom4j) 解析器对象，将 xml 文件解析成 document 对象 <br>
     * 2. 创建 Configuration 全局配置对象, 作为传参向下传递 <br>
     * 3. xmlConfigBuilder.parse() 将 xml 里的 mappers 标签和 environment 标签进行解析，
     * 并将其封装成 mappedStatements 对象集合存放到 Configuration 里 <br>
     * <mappers> <==> Map<String, MappedStatement> mappedStatements
     *
     * 例如：全局配置文件为
     * <mappers>
     *      <mapper resource="mapper/User_Mapper.xml"/>
     *      <mapper resource="mapper/UserDao_Mapper.xml"/>
     *      <mapper resource="mapper/Student_Mapper.xml"/>
     *  </mappers>
     * 通过 parse()，<mappers> 下的所有子标签 <mapper> 将会被解析成 MappedStatement 对象
     * 并被维护到集合中 Map<String, MappedStatement>(mybatis 源码中是 ArrayList)
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
