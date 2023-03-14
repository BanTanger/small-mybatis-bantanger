package com.bantanger.mybatis.mapping;

/**
 * SQL 指令类型
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:08
 */
public enum SqlCommandType {

    /**
     * 未知
     */
    UNKNOWN,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查找
     */
    SELECT;

}
