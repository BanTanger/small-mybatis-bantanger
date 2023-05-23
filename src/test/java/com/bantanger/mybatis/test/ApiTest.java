package com.bantanger.mybatis.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.system.SystemUtil;
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:59
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_sqlSessionFactory() throws Exception {
        // =================可交付 spring 管理 ====================
        // 1. 将内存里缓存的字节流进行解析
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                // 通过类加载器将核心配置文件通过字节输入流方式加载到内存，解析交付给 SqlSessionFactory
                .build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));

            // 从 SqlSessionFactory 中获取 SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象(代理对象)
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        // ======================================================

        // 3. 测试验证
        User user = userDao.queryUserInfoById(10001L);
        logger.info("测试结果 {}", JSON.toJSONString(user));
    }

}
