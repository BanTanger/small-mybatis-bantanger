package com.bantanger.mybatis.binding;

import com.bantanger.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射器代理工厂
 * 封装 Mapper，如果没有这层封装，每次创建代理类的时候，都需要调用 Proxy.newProxyInstance
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:14
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Map<Method, MapperMethod> getMethodCache() {
        return methodCache;
    }

    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, methodCache, mapperInterface);
        // 创建代理对象之后，通过代理对象调用的任何方法都会交付给第三个参数 InvocationHandler 也就是这里的 mapperProxy 进行处理
        // 本质是 InvocationHandler 调用 invoke 方法反射执行代理对象的实际方法
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
