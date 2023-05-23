package com.bantanger.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * @author BanTanger 半糖
 * @Date 2023/5/23 12:18
 */
public class SetFieldInvoker implements Invoker {

    private Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        // set 方法只是设置值，并没有返回值，所以返回 null
        field.set(target, args[0]);
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
