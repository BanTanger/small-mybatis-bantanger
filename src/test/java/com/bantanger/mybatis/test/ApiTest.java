package com.bantanger.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.bantanger.mybatis.build.xml.XMLConfigBuilder;
import com.bantanger.mybatis.io.Resources;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.SqlSession;
import com.bantanger.mybatis.session.SqlSessionFactory;
import com.bantanger.mybatis.session.SqlSessionFactoryBuilder;
import com.bantanger.mybatis.session.defaults.DefaultSqlSession;
import com.bantanger.mybatis.test.dao.IUserDao;
import com.bantanger.mybatis.test.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:59
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_SqlSessionFactory() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        User user = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

    @Test
    public void test_selectOne() throws IOException {
        // 解析 XML
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 获取 DefaultSqlSession
        SqlSession sqlSession = new DefaultSqlSession(configuration);

        // 执行查询，默认为集合参数
        Object[] req = {1L};
        Object res = sqlSession.selectOne("com.bantanger.mybatis.test.dao.IUserDao.queryUserInfoById", req);
        logger.info("执行结果：{}", JSON.toJSONString(res));
    }

}
