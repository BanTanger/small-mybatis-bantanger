package com.bantanger.mybatis.dataSource.pooled;

import com.bantanger.mybatis.dataSource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * 有池化数据源工厂
 * 有池化工厂继承无池化工厂，这样可以减少 Properties 统一包装的反射方式的属性处理
 * 但目前只是简单的获取属性传参，还不能体现这种继承有多便捷
 * @author BanTanger 半糖
 * @Date 2023/3/18 22:42
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(props.getProperty("driver"));
        pooledDataSource.setUrl(props.getProperty("url"));
        pooledDataSource.setUsername(props.getProperty("username"));
        pooledDataSource.setPassword(props.getProperty("password"));
        return pooledDataSource;
    }
}
