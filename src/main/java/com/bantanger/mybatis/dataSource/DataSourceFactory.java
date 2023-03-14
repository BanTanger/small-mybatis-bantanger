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

    DataSource getDataSource();

}
