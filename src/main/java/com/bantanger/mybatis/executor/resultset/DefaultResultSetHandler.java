package com.bantanger.mybatis.executor.resultset;

import com.bantanger.mybatis.executor.Executor;
import com.bantanger.mybatis.mapping.BoundSql;
import com.bantanger.mybatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 默认 Map 结果处理器
 * @author BanTanger 半糖
 * @Date 2023/3/19 23:31
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private final BoundSql boundSql;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public <E> List<E> handleResultSets(Statement stmt) throws SQLException {
        ResultSet resultSet = stmt.getResultSet();
        try {
            return resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将结果集封装成实体对象集合
     * @param resultSet sql 执行后的结果集
     * @param clazz 反射转化后的实体对象
     * @param <T>
     * @return
     */
    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try {
            // 元数据信息，包括字段名、字段值
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 字段列数量，例如 user 表有 user_id 和 username 两个字段, columnCount = 2
            int columnCount = metaData.getColumnCount();

            // 迭代器方式遍历结果集行值
            while (resultSet.next()) {
                T obj = (T) clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    // 字段值
                    Object value = resultSet.getObject(i);
                    // 字段名
                    String columnName = metaData.getColumnName(i);
                    // 一一获取实体对象方法名，之后采用反射方法调度
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, Date.class);
                    } else {
                        method = clazz.getMethod(setMethod, value.getClass());
                    }
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
