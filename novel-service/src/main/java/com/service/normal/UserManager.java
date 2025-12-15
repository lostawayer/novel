package com.service.normal;

import com.commons.data.DataPage;
import com.domain.User;
import com.event.UserEvent;
import com.persistence.IUserPersistence;
import com.service.IUserManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;


@Service(IUserManager.ID)
@ConditionalOnProperty(name = "medical.com.service.type",havingValue = "userManager1")
@Transactional(readOnly = true)
@CacheConfig(cacheNames = {IUserManager.ID})//为当前类中所有缓存注解
public class  UserManager implements IUserManager, ApplicationEventPublisherAware {
    @Autowired
    private PasswordEncoder  passwordEncoder;

    @Autowired
    private IUserPersistence userPersistence;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true,beforeInvocation = true)
    public void addOrUpdateUser(User user) {
        String username = user.getUsername();
        Long id = user.getId();
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        User dbUser = null;
        UserEvent userEvent = null;


        if (id != null && id > 0) {
            dbUser = this.userPersistence.getUserByName(username);
            Assert.notNull(dbUser, "to update user not found");
            BeanUtils.copyProperties(user, dbUser,  "id", "username");
            userEvent = new UserEvent(dbUser, UserEvent.UserEventType.update);
        } else {//新增
            boolean isUserNameExist = this.userPersistence.isUserNameExist(username);
            Assert.isTrue(!isUserNameExist, "username already exist");
            dbUser = user;
            userEvent = new UserEvent(dbUser, UserEvent.UserEventType.insert);
        }
        this.userPersistence.saveUser(dbUser);
        this.applicationEventPublisher.publishEvent(userEvent);
    }

    @Override
    @Cacheable(key = "#root.methodName+#root.args[0]")
    public User getUserByName(String username) {
        return this.userPersistence.getUserByName(username);
    }

    @Override
    @Cacheable(key = "#root.methodName+#root.args[0]")
    public boolean isUserNameExist(String username) {
        return this.userPersistence.isUserNameExist(username);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(allEntries = true,beforeInvocation = true)
    public void DelectUser(String... usernames) {
        this.userPersistence.DelectUser(usernames);
    }


    @Override
    @Cacheable(key = "#root.methodName+#root.args[0]+#root.args[1]")
    public DataPage<User> findUser(int pageNumber, int pageSize) {
        return this.userPersistence.findUser(pageNumber, pageSize);
    }

    @Override
    @CacheEvict(allEntries = true,beforeInvocation = true)
    public DataPage<User> findUserFtl(int pageNumber, int pageSize) {
        return this.userPersistence.findUserFtl(pageNumber, pageSize);
    }

    @Override
    public List<String> findRoleName(String username) {
        return List.of(username);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
