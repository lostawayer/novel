package com.persistence;

import com.commons.data.DataPage;
import com.domain.User;

import java.util.List;

//持久化接口
public interface IUserPersistence {
    void saveUser(User user);

    User getUserByName(String username);

    boolean isUserNameExist(String username);

    void DelectUser(String... usernames);

    DataPage<User> findUser(int pageNumber, int pageSize);
    DataPage<User> findUserFtl(int pageNumber, int pageSize);
}
