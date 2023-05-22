package com.bantanger.mybatis.mapping;

import com.bantanger.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * 环境 and 建造者模式
 *
 * @author BanTanger 半糖
 * @Date 2023/3/14 13:00
 */
public class Environment {

    /*
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
     */

    /**
     * 环境 ID
     * 对应 <\environment id="development">
     */
    private final String id;
    /**
     * 事务工厂
     */
    private final TransactionFactory transactionFactory;
    /**
     * 数据源
     * 对应 <\dataSource> 标签
     * <dataSource type="POOLED"> type 表示选择池化数据源技术，还有 UNPOOLED 无池化技术
     *      <property name="driver" value="com.mysql.jdbc.Driver"/>
     *      <property name="url" value="jdbc:mysql://127.0.0.1:3306/jdbc?useUnicode=true"/>
     *      <property name="username" value="root"/>
     *      <property name="password" value="123456"/>
     * </dataSource>
     */
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {

        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource);
        }

    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
