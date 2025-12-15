package com.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.commons.data.DataPage;
import com.domain.User;
import com.persistence.IUserPersistence;
import com.persistence.mybatis.mapper.IUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository//持久化
public class UserPersistenceMybatis implements IUserPersistence {
    @Autowired//注入
    private IUserMapper userMapper;

    @Override//重写父类方法
    public void saveUser(User user) {
        this.userMapper.insertOrUpdate(user);
    }

    @Override
    public User getUserByName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.userMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean isUserNameExist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.userMapper.exists(queryWrapper);
    }

    @Override
    public void DelectUser(String... usernames) {
        this.userMapper.DelectUser(usernames);
    }

    @Override
    public DataPage<User> findUser(int pageNumber, int pageSize) {
        Page page = new Page(pageNumber, pageSize);
        page.addOrder(OrderItem.desc("username"));
        Page result = this.userMapper.selectPage(page, null);
        return DataPage.createPage(result.getRecords(),pageNumber,pageSize,result.getTotal());
    }

    @Override
    public DataPage<User> findUserFtl(int pageNumber, int pageSize) {
        Page page = new Page(pageNumber, pageSize);
        page.addOrder(OrderItem.desc("username"));
        Page result = this.userMapper.selectPage(page, null);
        return DataPage.createPage(result.getRecords(),pageNumber,pageSize,result.getTotal());
    }
}

