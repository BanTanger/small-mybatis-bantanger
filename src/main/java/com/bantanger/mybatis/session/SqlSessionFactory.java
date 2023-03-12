package com.bantanger.mybatis.session;

/**
 * 工厂模式接口，构建 SqlSession 的工厂
 * @author BanTanger 半糖
 * @Date 2023/3/10 18:20
 */
public interface SqlSessionFactory {

    /**
     * 打开一个 session
     * @return
     */
    SqlSession openSession();

}
