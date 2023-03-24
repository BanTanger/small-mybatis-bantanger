package com.bantanger.mybatis.dataSource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.bantanger.mybatis.dataSource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Druid 数据源工厂
 * @author BanTanger 半糖
 * @Date 2023/3/14 12:50
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        // 配置动态数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }
}
