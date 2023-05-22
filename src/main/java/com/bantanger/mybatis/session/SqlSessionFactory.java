package com.bantanger.mybatis.session;

/**
 * 工厂模式接口，构建 SqlSession 的工厂
 * @author BanTanger 半糖
 * @Date 2023/3/10 18:20
 */
public interface SqlSessionFactory {

    /**
     * 1. 生产一个 sqlSession 对象
     * 2. 创建执行器对象
     * @return
     */
    SqlSession openSession();

}
