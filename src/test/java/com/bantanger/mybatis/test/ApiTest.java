package com.bantanger.mybatis.test;

import com.alibaba.fastjson.JSON;
import com.bantanger.mybatis.build.xml.XMLConfigBuilder;
import com.bantanger.mybatis.dataSource.pooled.PooledDataSource;
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
import java.sql.Connection;
import java.sql.SQLException;

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
        for (int i = 0; i < 50; i++) {
            User user = userDao.queryUserInfoById(1L);
            logger.info("测试结果：{} i = {}", JSON.toJSONString(user), i);
        }
    }

    @Test
    public void test_pooled() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/jdbc?useUnicode=true");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("123456");
        // 持续获得连接
        while(true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            // 是否关闭连接
//            connection.close();
        }
    }

}
