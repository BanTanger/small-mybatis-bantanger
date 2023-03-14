package com.bantanger.mybatis.build;

import com.bantanger.mybatis.session.Configuration;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:27
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
