package com.bantanger.mybatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 结果集处理器
 * @author BanTanger 半糖
 * @Date 2023/3/19 23:23
 */
public interface ResultSetHandler {

    /**
     * 通过反射技术实现
     * 根据字段名和实体属性名对应关系自动完成映射封装
     * @param stmt
     * @param <E>
     * @return
     * @throws SQLException
     */
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

}
