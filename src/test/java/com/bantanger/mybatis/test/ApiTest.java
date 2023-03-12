package com.bantanger.mybatis.test;

import com.bantanger.mybatis.binding.MapperProxyFactory;
import com.bantanger.mybatis.binding.MapperRegistry;
import com.bantanger.mybatis.session.SqlSession;
import com.bantanger.mybatis.session.defaults.DefaultSqlSessionFactory;
import com.bantanger.mybatis.test.dao.IUserDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:59
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_MapperProxyFactory() {
        // 1. 注册 Mapper
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("com.bantanger.mybatis.test.dao");

        // 2. 从 SqlSession 工厂获取 Session
        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 3. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 4. 测试验证
        String res = userDao.queryUserName("10001");
        logger.info("测试结果：{}", res);
    }

}
