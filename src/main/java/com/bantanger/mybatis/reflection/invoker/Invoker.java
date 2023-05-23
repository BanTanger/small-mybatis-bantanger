package com.bantanger.mybatis.reflection.invoker;

/**
 * 获取设置对象类中的属性值，可以是 Field 字段的 get/set 方法调用，以及普通的 Method 方法调用
 * 为了减少 if-else 次数，可以吧集中的调用者的实现类包装成调用策略，统一接口的不同策略的实现类
 *
 * 任何方法的调用都离不开对象和入参，将这两个字段和返回结果定义成通用方法可以包装成不同策略的实现类
 * @author BanTanger 半糖
 * @Date 2023/5/23 11:33
 */
public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();

}
