package com.bantanger.mybatis.dataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源配置工厂
 * @author BanTanger 半糖
 * @Date 2023/3/14 12:49
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    /**
     * 简单包装 getDataSource 获取数据源
     * 把必要的 driver、url、username、password 参数传进去
     * 在源码种，这一部分是通过大量的反射字段处理方式存放和获取的
     * @return
     */
    DataSource getDataSource();

}
