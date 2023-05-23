package com.bantanger.mybatis.binding;

import com.bantanger.mybatis.mapping.MappedStatement;
import com.bantanger.mybatis.mapping.SqlCommandType;
import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/13 16:16
 */
public class MapperMethod {

    /**
     * 封装 sqlCommand，便于获取 statementId 唯一标识
     */
    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.command = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (command.getType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(command.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }

    /**
     * SQL 指令
     */
    public static class SqlCommand {

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            // 拼接 statementName 也就是 statementId
            String statementName = mapperInterface.getName() + "." + method.getName();
            // 获取具体 mappedStatement 对象
            MappedStatement ms = configuration.getMappedStatement(statementName);
            // sql 方法名称，就是 method.getName
            name = ms.getId();
            // sql 的标签类型
            type = ms.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }

}