package com.bantanger.mybatis.session;

/**
 * SqlSession 用于执行 SQL，获取映射器，管理事务
 * 通常情况下，在应用程序中使用的 MyBatis 的 API 就是这个接口定义的
 * 用来执行 SQL、获取映射器对象以及后续管理事务操作的标准接口
 *
 * @author BanTanger 半糖
 * @Date 2023/3/10 18:14
 */
public interface SqlSession {

    /**
     * 根据指定的 SqlID 获取一条记录的封装对象
     * @param statement sqlID
     * @param <T> 封装之后的对象类型
     * @return Mapper Object 封装之后的对象
     */
    <T> T selectOne(String statement);

    /**
     * 根据指定的 sqlID 获取一条记录的封装对象，只不过这个方法允许我们传递一些参数给 sql
     * 在一般的使用中，这个参数用于传递 pojo，Map 或者 ImmutableMap
     * @param statement
     * @param parameter
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement, Object parameter);

    Configuration getConfiguration();

    /**
     * 获得映射器，这个方法巧妙的使用了泛型，使类型安全
     * @param type
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> type);

}
