package com.bantanger.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * @author BanTanger 半糖
 * @Date 2023/5/23 12:18
 */
public class GetFieldInvoker implements Invoker {

    private Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
