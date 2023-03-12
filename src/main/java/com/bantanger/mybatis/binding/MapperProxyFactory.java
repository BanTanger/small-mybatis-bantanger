package com.bantanger.mybatis.binding;

import com.bantanger.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 映射器代理工厂
 * 封装 Mapper，如果没有这层封装，每次创建代理类的时候，都需要调用 Proxy.newProxyInstance
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:14
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
