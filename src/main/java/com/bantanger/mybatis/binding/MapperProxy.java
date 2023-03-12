package com.bantanger.mybatis.binding;

import com.bantanger.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 映射器代理类
 * @author BanTanger 半糖
 * @Date 2023/3/6 20:09
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;

    private SqlSession sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            // 如果是 Object 提供的 toString, hashCode 则直接返回调用
            return method.invoke(this, args);
        } else {
            // 拦截到需要代理的方法，调用 sqlSession 缓存的代理操作
            return sqlSession.selectOne(method.getName(), args);
        }
    }

}
