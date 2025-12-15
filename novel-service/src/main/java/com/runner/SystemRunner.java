package com.runner;

import com.domain.User;
import com.service.IUserManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ConfigurationProperties(prefix = "medical.security")
public class SystemRunner implements ApplicationRunner {
    @Getter @Setter
    private User user;

    @Autowired
    private IUserManager userManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!this.userManager.isUserNameExist(this.user.getUsername())) {
            this.userManager.addOrUpdateUser(this.user);
            System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
    }
}
