package com.bantanger.mybatis.mapping;

import com.bantanger.mybatis.session.Configuration;

import java.util.Map;

/**
 * 映射语句对象
 * 用于存放 mapper.xml 解析内容
 * 每一个 <select>|<update>|<delete>|<insert> 标签都是一个 MappedStatement 对象
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:02
 */
public class MappedStatement {

    /*
     * 对应 mapper.xml
     * <select id="queryUserInfoById"
     *          parameterType="java.lang.Long"
     *          resultType="com.bantanger.mybatis.test.po.User">
     *
     *     select id, userId, userHead, createTime
     *     from user
     *     where id = #{id};
     *
     * </select>
     */

    /**
     * 对应标签里的 id = "queryUserInfoById"
     * 每一个 mappedStatement 的唯一标识
     */
    private String id;

    private Configuration configuration;

    /**
     * sql 标签类型：select、update、insert、delete
     */
    private SqlCommandType sqlCommandType;

    /**
     * sql 标签
     */
    private BoundSql boundSql;

    public MappedStatement() {
    }

    /**
     * 建造者模式构造对象
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, BoundSql boundSql) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.boundSql = boundSql;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }

    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }

}
