package com.bantanger.mybatis.test.dao;

import com.bantanger.mybatis.test.po.User;

/**
 * @author BanTanger 半糖
 * @Date 2023/3/6 21:00
 */
public interface IUserDao {

    User queryUserInfoById(Long uId);

}
