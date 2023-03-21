package com.bantanger.mybatis.mapping;

import java.util.Map;

/**
 * 绑定的 SQL, 是从 SqlSource 而来，将动态内容都处理完成得到的 SQL 语句字符串，其中包括占位符 ?,还有绑定的参数
 * @author BanTanger 半糖
 * @Date 2023/3/14 13:18
 */
public class BoundSql {

    /**
     * 最终替换了占位符的 sql 语句
     * select id, userId, userHead, createTime from user where id = ?;
     */
    private String sql;

    /**
     * #{} 里面的值的一个集合
     * key：原始 sql 语句中参数的序号，value：#{} 里面的内容，如 id
     * 之后根据 value 与用户传入的参数对象反射获取具体成员变量值
     * id == user.id
     */
    private Map<Integer, String> parameterMappings;

    /**
     * 传入值的参数类型
     * where id = #{id} 中 id 的具体参数类型指定
     */
    private String parameterType;

    /**
     * 返回值的参数类型
     * 整个 select 查询的列集合对象
     * select user_name, user_id form user
     * resultType = "com.bantanger.pojo.user"
     */
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }

}
