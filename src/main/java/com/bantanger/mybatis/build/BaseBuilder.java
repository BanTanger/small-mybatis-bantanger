package com.bantanger.mybatis.build;

import com.bantanger.mybatis.session.Configuration;
import com.bantanger.mybatis.type.TypeAliasRegistry;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/13 13:27
 */
public abstract class BaseBuilder {

    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
