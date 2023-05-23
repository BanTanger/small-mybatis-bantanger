package com.bantanger.mybatis.reflection.wrapper;

/**
 * 对象包装工厂
 * @author BanTanger 半糖
 * @Date 2023/5/23 21:44
 */
public interface ObjectWrapperFactory {

    /**
     * 判断有没有包装器
     */
    boolean hasWrapperFor(Object object);

    /**
     * 得到包装器
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
