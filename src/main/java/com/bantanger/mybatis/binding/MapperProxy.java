package com.bantanger.mybatis.binding;

import com.bantanger.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 映射器代理类
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:09
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;

    private SqlSession sqlSession;
    private final Class<T> mapperInterface;
    /**
     * 将方法缓存到内存中，节省资源开销，因为数据库方法调用无非也就那几个
     * selectOne、selectList、Update、Delete、Insert
     */
    private final Map<Method, MapperMethod> methodCache;

    public MapperProxy(SqlSession sqlSession, Map<Method, MapperMethod> methodCache, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }

    /**
     * 通过反射调用各种方法
     * @param proxy 代理对象的引用，很少使用
     * @param method 被调用方法的字节码对象
     * @param args 被调用方法的形参列表
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            // 如果是 Object 提供的 toString, hashCode 则直接返回调用
            return method.invoke(this, args);
        } else {
            final MapperMethod mapperMethod = cachedMapperMethod(method);
            // 本质还是执行底层 JDBC 方法, 但这里传递 sqlSession, sqlSession 再通过 executor 执行方法
            // 并通过 mapperMethod 做一层封装
            return mapperMethod.execute(sqlSession, args);
        }
    }

    /**
     * 去缓存中找 MapperMethod
     */
    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            //找不到才去 new
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }

}
