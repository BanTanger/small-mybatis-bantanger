整个 mybatis 源码从狭义的角度来说就是对原始 JDBC 进行封装抽象

思考一下我们没有接触到 mybatis 前是怎么通过 JDBC 进行数据库连接的呢? 

给一段 JDBC 代码参考：

```java 
    Connection connection = null; // 数据库驱动
    PreparedStatement prepareStatement = null; // sql 预处理对象
    ResultSet result = null; // 结果集对象
    try {
        // 加载数据库驱动
        Class.forName("com.mysql.jdbc.Driver");
        // 通过驱动管理类获取数据库连接
        connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/数据库名?characterEncoding=utf-8", 
            "用户名", "密码");
        // 定义 sql 语句, 原始 JDBC 只能处理 ? 占位符
        String sql = "select user_head from user where user_id = ?, username = ?";
        // 获取预处理后的 statement 
        prepareStatement = connection.prepareStatement(sql);
        // 设置参数, 第一个参数为 sql 语句中参数的序号(从1开始), 第二个参数为设置的参数值
        prepareStatement.setInteger(1, 10001); // 对应 sql 语句的 user_id = 10001;
        prepareStatement.setString(2, "tom"); // 对应 sql 语句的 username = "tom";
        
        // 向数据库发送 sql 执行查询请求，查询出结果集
        resultSet = prepareStatement.executeQuery();
        // 遍历查询结果集
        while(resultSet.next()) {
            String userHead = resultSet.getString("user_head");
            // 封装 User 对象
            user.setId(10001);
            user.setUsername("tom");
            user.setHead(userHead);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // 释放资源
        if (connection != null) {
            connection.close();
        }
    }
```

我们来思考一下这段 JDBC 代码存在哪些问题

1. 数据库配置信息硬编码
2. 频繁创建和释放数据库连接资源
3. sql 语句、参数、返回结果集获取都存在硬编码问题
4. 需要手动封装返回结果集，较为繁琐

为此，mybatis 有哪些应对措施呢?

1. 数据库配置信息硬编码 --> 配置文件封装
2. 频繁创建和释放数据库连接资源 --> 享元模式连接池节省资源开销
3. sql 语句、参数、返回结果集获取都存在硬编码问题 --> 配置文件封装
4. 需要手动封装返回结果集，较为繁琐 --> 反射技术根据字段名和实体属性名对应关系自动完成映射封装

那我们就可以做一个简单的总结了

Mybatis 作为一个半 ORM 框架，本质就是对 JDBC 进行了封装抽象

不过在实现的过程中，通过采用配置文件、数据库连接池、反射、内省等技术把原始 JDBC 所存在的问题进行了规避

## Mybatis 文件格式

mybatis 中 xml 语法书写总体配置文件 `mybatis-config-datasource.xml`

mybatis 资源读取器只会读取这个配置文件，对应的 mapper 集成在这里，这样的好处是只调用一次 getResourceAsStream(String resource)

需要注意的是，springboot 可以通过 yml 文件设置 configuration，并且根据约定大于配置的思想，
只要 springboot 项目里的 mapper.xml 文件放在对应的路径便可扫描到。也就是说无需总体配置文件`mybatis-config-datasource.xml`

本项目目前还没集成到 springboot

```xml
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
<!--            <dataSource type="UNPOOLED">-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://127.0.0.1:3306/jdbc?useUnicode=true"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="mapper/User_Mapper.xml"/>
    </mappers>
</configuration>
```

mybatis 中 xml 语法书写 sql 语句

```xml
<mapper namespace="com.bantanger.mybatis.test.dao.IUserDao">
    <select id="queryUserInfoById" parameterType="java.lang.Long" resultType="com.bantanger.mybatis.test.po.User">
        select id, userId, userHead, createTime
        from user
        where id = #{id};
    </select>
</mapper>
```

资源读取 `XMLConfigBuilder`

mybatis 怎么对这个 xml 标签进行拆解的

+ 原始 sql 语句通过 **对象处理器** `StatementHandler` 进行 sql 参数预处理，将占位符替换成 ?, 并将参数名称缓存到 parameterMappings 集合里
+ 最终 sql 语句执行委托给 **执行器** `Executor`, 执行器调用底层封装的 JDBC 接口
+ 结果集对象 resultSet 交付给 **结果集处理器** `ResultSetHandler`, 通过反射技术将结果集自动封装成实体对象 